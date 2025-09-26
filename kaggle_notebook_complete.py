#!/usr/bin/env python3
"""
Complete Kaggle Notebook for FOCUS Training
Copy this entire code into Kaggle notebook cells
"""

# ==========================================
# CELL 1: Install Dependencies
# ==========================================

import subprocess
import sys

def install_packages():
    packages = [
        'openslide-python',
        'opencv-python',
        'Pillow',
        'scikit-learn',
        'tqdm'
    ]
    
    for package in packages:
        try:
            print(f"Installing {package}...")
            subprocess.check_call([sys.executable, "-m", "pip", "install", package])
        except Exception as e:
            print(f"Warning: Could not install {package}: {e}")

install_packages()

# ==========================================
# CELL 2: Import Libraries and Setup
# ==========================================

import os
import sys
import numpy as np
import pandas as pd
import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.utils.data import Dataset, DataLoader
import torchvision.transforms as transforms
from PIL import Image
import cv2
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
import multiprocessing as mp
from functools import partial
import json
import pickle
from pathlib import Path
import logging
from typing import List, Tuple, Dict, Optional
import warnings
import time
from tqdm import tqdm
warnings.filterwarnings('ignore')

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

print("✓ All libraries imported successfully!")
print(f"PyTorch version: {torch.__version__}")
print(f"CUDA available: {torch.cuda.is_available()}")
print(f"CPU cores: {mp.cpu_count()}")

# ==========================================
# CELL 3: FOCUS Model Definitions
# ==========================================

class AdaptiveVisualCompression(nn.Module):
    """FOCUS: Knowledge-Enhanced Adaptive Visual Compression"""
    def __init__(self, input_dim=2048, compress_dim=512, num_heads=8):
        super().__init__()
        self.input_dim = input_dim
        self.compress_dim = compress_dim
        
        # Multi-head attention for adaptive compression
        self.attention = nn.MultiheadAttention(
            embed_dim=input_dim,
            num_heads=num_heads,
            batch_first=True
        )
        
        # Compression layers
        self.compress_layers = nn.Sequential(
            nn.Linear(input_dim, compress_dim * 2),
            nn.ReLU(),
            nn.Dropout(0.1),
            nn.Linear(compress_dim * 2, compress_dim),
            nn.LayerNorm(compress_dim)
        )
        
        # Knowledge enhancement module
        self.knowledge_fusion = nn.Sequential(
            nn.Linear(compress_dim, compress_dim),
            nn.ReLU(),
            nn.Linear(compress_dim, compress_dim)
        )
        
    def forward(self, features, knowledge_features=None):
        # Adaptive attention-based compression
        attended_features, attention_weights = self.attention(
            features, features, features
        )
        
        # Compress features
        compressed = self.compress_layers(attended_features)
        
        # Knowledge enhancement if available
        if knowledge_features is not None:
            compressed = compressed + self.knowledge_fusion(knowledge_features)
        
        # Global pooling for WSI-level representation
        wsi_features = torch.mean(compressed, dim=1)
        
        return wsi_features, compressed, attention_weights

class FOCUSClassifier(nn.Module):
    """FOCUS Few-Shot WSI Classifier"""
    def __init__(self, feature_dim=512, num_classes=2):
        super().__init__()
        self.feature_dim = feature_dim
        self.num_classes = num_classes
        
        # Feature compression and enhancement
        self.adaptive_compression = AdaptiveVisualCompression(
            input_dim=2048, compress_dim=feature_dim
        )
        
        # Few-shot learning components
        self.prototype_layer = nn.Parameter(
            torch.randn(num_classes, feature_dim)
        )
        
        # Classification head
        self.classifier = nn.Sequential(
            nn.Linear(feature_dim, feature_dim // 2),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(feature_dim // 2, num_classes)
        )
        
    def forward(self, wsi_features):
        # Apply FOCUS adaptive compression
        compressed_features, _, attention_weights = self.adaptive_compression(wsi_features)
        
        # Few-shot prototype matching
        distances = torch.cdist(compressed_features.unsqueeze(1), 
                               self.prototype_layer.unsqueeze(0))
        prototype_scores = -distances.squeeze(1)
        
        # Final classification
        class_logits = self.classifier(compressed_features)
        
        # Combine prototype and classifier scores
        final_scores = 0.7 * class_logits + 0.3 * prototype_scores
        
        return final_scores, attention_weights

print("✓ FOCUS model classes defined!")

# ==========================================
# CELL 4: Feature Extraction and MapReduce
# ==========================================

class FeatureExtractor:
    """Feature extraction with ResNet50"""
    def __init__(self, device='cuda'):
        self.device = device
        self.model = self._load_model()
        self.transform = self._get_transforms()
        
    def _load_model(self):
        import torchvision.models as models
        model = models.resnet50(pretrained=True)
        model.fc = nn.Identity()  # Remove final layer
        model.eval()
        return model.to(self.device)
    
    def _get_transforms(self):
        return transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], 
                               std=[0.229, 0.224, 0.225])
        ])
    
    def extract_patch_features(self, patch):
        with torch.no_grad():
            if isinstance(patch, np.ndarray):
                patch = Image.fromarray(patch)
            
            patch_tensor = self.transform(patch).unsqueeze(0).to(self.device)
            features = self.model(patch_tensor)
            return features.cpu().numpy().flatten()

class MapReduceFeatureExtractor:
    """MapReduce implementation for feature extraction"""
    def __init__(self, feature_extractor, num_workers=4):
        self.feature_extractor = feature_extractor
        self.num_workers = num_workers
        
    def extract_features_batch(self, patches):
        """Extract features from patch batch"""
        features = []
        for patch in patches:
            try:
                feat = self.feature_extractor.extract_patch_features(patch)
                features.append(feat)
            except Exception as e:
                features.append(np.zeros(2048))  # Default feature size
        return np.array(features)
    
    def extract_wsi_features(self, patches):
        """Extract features from WSI patches using MapReduce"""
        if len(patches) == 0:
            return np.array([])
        
        # For Kaggle, process sequentially to avoid memory issues
        all_features = []
        batch_size = 10  # Small batch size for memory efficiency
        
        for i in tqdm(range(0, len(patches), batch_size), desc="Extracting features"):
            batch = patches[i:i+batch_size]
            batch_features = self.extract_features_batch(batch)
            all_features.append(batch_features)
        
        if all_features:
            return np.vstack(all_features)
        else:
            return np.array([])

print("✓ Feature extraction classes defined!")

# ==========================================
# CELL 5: WSI Processing
# ==========================================

class WSIProcessor:
    """Whole Slide Image processor"""
    def __init__(self, patch_size=224, overlap=0.5, magnification='20x'):
        self.patch_size = patch_size
        self.overlap = overlap
        self.magnification = magnification
    
    def extract_patches_from_wsi(self, wsi_path, max_patches=500):
        """Extract patches from WSI file"""
        try:
            return self._extract_patches_tif(wsi_path, max_patches)
        except Exception as e:
            logger.error(f"Error processing WSI {wsi_path}: {e}")
            return []
    
    def _extract_patches_tif(self, wsi_path, max_patches):
        """Extract patches from TIFF file"""
        img = cv2.imread(wsi_path)
        if img is None:
            return []
        
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        h, w = img.shape[:2]
        
        patches = []
        step = int(self.patch_size * (1 - self.overlap))
        
        for y in range(0, h - self.patch_size, step):
            for x in range(0, w - self.patch_size, step):
                patch = img[y:y+self.patch_size, x:x+self.patch_size]
                
                # Skip patches with too much background
                if self._is_tissue_patch(patch):
                    patches.append(patch)
                    
                if len(patches) >= max_patches:
                    return patches
        
        return patches
    
    def _is_tissue_patch(self, patch, tissue_threshold=0.8):
        """Check if patch contains enough tissue"""
        gray = cv2.cvtColor(patch, cv2.COLOR_RGB2GRAY)
        tissue_mask = gray < 230  # Non-white pixels
        tissue_ratio = np.sum(tissue_mask) / tissue_mask.size
        return tissue_ratio > (1 - tissue_threshold)

print("✓ WSI processor defined!")

# ==========================================
# CELL 6: Dataset Class
# ==========================================

class CAMELYONDataset:
    """CAMELYON Dataset for few-shot learning"""
    def __init__(self, data_dir, max_patches=300):
        self.data_dir = Path(data_dir)
        self.max_patches = max_patches
        
        # Setup WSI processor
        self.wsi_processor = WSIProcessor()
        
        # Find all WSI files
        self.samples = self._find_all_samples()
        
    def _find_all_samples(self):
        """Find all WSI samples"""
        samples = []
        
        # Normal samples
        normal_dir = self.data_dir / 'raw_data' / 'wsi_images' / 'normal'
        if normal_dir.exists():
            for file_path in normal_dir.glob('*.tif'):
                samples.append({
                    'path': str(file_path),
                    'label': 0,  # Normal
                    'filename': file_path.name
                })
        
        # Tumor samples (note: "turnor" typo in your dataset)
        tumor_dir = self.data_dir / 'raw_data' / 'wsi_images' / 'turnor'
        if tumor_dir.exists():
            for file_path in tumor_dir.glob('*.tif'):
                samples.append({
                    'path': str(file_path),
                    'label': 1,  # Tumor
                    'filename': file_path.name
                })
        
        return samples
    
    def __len__(self):
        return len(self.samples)
    
    def get_sample(self, idx):
        """Get sample by index"""
        sample = self.samples[idx]
        
        # Extract patches
        patches = self.wsi_processor.extract_patches_from_wsi(
            sample['path'], self.max_patches
        )
        
        if len(patches) == 0:
            # Return dummy data if no patches found
            patches = [np.zeros((224, 224, 3), dtype=np.uint8)]
        
        return {
            'patches': patches,
            'label': sample['label'],
            'filename': sample['filename'],
            'path': sample['path']
        }

print("✓ Dataset class defined!")

# ==========================================
# CELL 7: Check Dataset
# ==========================================

# Check if dataset exists and explore structure
DATA_DIR = "/kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset"

if os.path.exists(DATA_DIR):
    print(f"✓ Dataset found at: {DATA_DIR}")
    
    # Check structure
    data_path = Path(DATA_DIR)
    print("\nDataset structure:")
    for root, dirs, files in os.walk(DATA_DIR):
        level = root.replace(DATA_DIR, '').count(os.sep)
        indent = ' ' * 2 * level
        print(f"{indent}{os.path.basename(root)}/")
        subindent = ' ' * 2 * (level + 1)
        for file in files[:5]:  # Show first 5 files
            print(f"{subindent}{file}")
        if len(files) > 5:
            print(f"{subindent}... and {len(files)-5} more files")
    
    # Create dataset
    dataset = CAMELYONDataset(DATA_DIR, max_patches=100)  # Reduced for quick test
    print(f"\n✓ Dataset created with {len(dataset)} samples")
    
    # Test one sample
    if len(dataset) > 0:
        print("\nTesting sample extraction...")
        sample = dataset.get_sample(0)
        print(f"Sample 0: {sample['filename']}")
        print(f"Label: {sample['label']} ({'Tumor' if sample['label'] else 'Normal'})")
        print(f"Patches extracted: {len(sample['patches'])}")
        print(f"Patch shape: {sample['patches'][0].shape if len(sample['patches']) > 0 else 'None'}")
else:
    print(f"✗ Dataset not found at: {DATA_DIR}")
    print("Please check dataset path and structure")

# ==========================================
# CELL 8: Training Function
# ==========================================

def train_focus_model(data_dir, output_dir="/kaggle/working", num_epochs=20):
    """Train FOCUS model"""
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    print(f"Using device: {device}")
    
    # Create output directory
    output_path = Path(output_dir)
    output_path.mkdir(exist_ok=True)
    
    # Create dataset
    dataset = CAMELYONDataset(data_dir, max_patches=200)  # Moderate number for training
    print(f"Dataset size: {len(dataset)} samples")
    
    if len(dataset) == 0:
        print("No samples found! Check dataset structure.")
        return None, 0.0
    
    # Split dataset into train/val
    total_samples = len(dataset)
    train_size = int(0.8 * total_samples)
    train_indices = list(range(train_size))
    val_indices = list(range(train_size, total_samples))
    
    print(f"Train samples: {len(train_indices)}")
    print(f"Val samples: {len(val_indices)}")
    
    # Setup feature extractor
    feature_extractor = FeatureExtractor(device=device)
    mapreduce_extractor = MapReduceFeatureExtractor(feature_extractor)
    
    # Initialize model
    model = FOCUSClassifier(feature_dim=512, num_classes=2).to(device)
    
    # Setup training
    optimizer = torch.optim.AdamW(model.parameters(), lr=1e-4, weight_decay=1e-5)
    criterion = nn.CrossEntropyLoss()
    scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=num_epochs)
    
    best_acc = 0.0
    train_losses = []
    val_accuracies = []
    
    for epoch in range(num_epochs):
        print(f"\nEpoch {epoch+1}/{num_epochs}")
        print("-" * 50)
        
        # Training phase
        model.train()
        train_loss = 0.0
        train_correct = 0
        train_total = 0
        
        for idx in tqdm(train_indices, desc="Training"):
            try:
                # Get sample
                sample = dataset.get_sample(idx)
                
                # Extract features using MapReduce
                wsi_features = mapreduce_extractor.extract_wsi_features(sample['patches'])
                
                if len(wsi_features) == 0:
                    continue
                
                # Convert to tensor
                wsi_features = torch.FloatTensor(wsi_features).unsqueeze(0).to(device)
                label = torch.LongTensor([sample['label']]).to(device)
                
                # Forward pass
                optimizer.zero_grad()
                outputs, attention_weights = model(wsi_features)
                loss = criterion(outputs, label)
                
                # Backward pass
                loss.backward()
                optimizer.step()
                
                # Statistics
                train_loss += loss.item()
                _, predicted = torch.max(outputs.data, 1)
                train_total += 1
                train_correct += (predicted == label).sum().item()
                
            except Exception as e:
                print(f"Training error for sample {idx}: {e}")
                continue
        
        # Validation phase
        model.eval()
        val_correct = 0
        val_total = 0
        
        with torch.no_grad():
            for idx in tqdm(val_indices, desc="Validation"):
                try:
                    sample = dataset.get_sample(idx)
                    wsi_features = mapreduce_extractor.extract_wsi_features(sample['patches'])
                    
                    if len(wsi_features) == 0:
                        continue
                    
                    wsi_features = torch.FloatTensor(wsi_features).unsqueeze(0).to(device)
                    label = torch.LongTensor([sample['label']]).to(device)
                    
                    outputs, _ = model(wsi_features)
                    _, predicted = torch.max(outputs.data, 1)
                    val_total += 1
                    val_correct += (predicted == label).sum().item()
                    
                except Exception as e:
                    print(f"Validation error for sample {idx}: {e}")
                    continue
        
        # Calculate metrics
        train_acc = 100 * train_correct / max(train_total, 1)
        val_acc = 100 * val_correct / max(val_total, 1)
        avg_train_loss = train_loss / max(train_total, 1)
        
        train_losses.append(avg_train_loss)
        val_accuracies.append(val_acc)
        
        print(f'Train Loss: {avg_train_loss:.4f}, Train Acc: {train_acc:.2f}%')
        print(f'Val Acc: {val_acc:.2f}%')
        
        # Save best model
        if val_acc > best_acc:
            best_acc = val_acc
            torch.save({
                'epoch': epoch,
                'model_state_dict': model.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'best_acc': best_acc,
                'train_losses': train_losses,
                'val_accuracies': val_accuracies
            }, output_path / 'best_focus_model.pth')
            
            print(f'✓ New best model saved! Accuracy: {best_acc:.2f}%')
        
        scheduler.step()
    
    print(f'\n🎉 Training completed! Best validation accuracy: {best_acc:.2f}%')
    return model, best_acc

print("✓ Training function defined!")

# ==========================================
# CELL 9: Run Training
# ==========================================

# Start training
print("🚀 Starting FOCUS training on CAMELYON dataset...")
print("="*60)

start_time = time.time()

# Train model
model, best_acc = train_focus_model(
    data_dir=DATA_DIR,
    output_dir="/kaggle/working",
    num_epochs=15  # Reduced for Kaggle time limits
)

end_time = time.time()
training_time = end_time - start_time

print(f"\n🎉 Training completed!")
print(f"Best accuracy: {best_acc:.2f}%")
print(f"Training time: {training_time/60:.1f} minutes")
print(f"Model saved to: /kaggle/working/best_focus_model.pth")

# ==========================================
# CELL 10: Test Model and Save Results
# ==========================================

# Test trained model on a few samples
if model is not None:
    print("\n🧪 Testing trained model...")
    
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model.eval()
    
    # Test on a few samples
    dataset = CAMELYONDataset(DATA_DIR, max_patches=100)
    feature_extractor = FeatureExtractor(device=device)
    mapreduce_extractor = MapReduceFeatureExtractor(feature_extractor)
    
    test_results = []
    
    for i in range(min(5, len(dataset))):  # Test on first 5 samples
        sample = dataset.get_sample(i)
        
        try:
            wsi_features = mapreduce_extractor.extract_wsi_features(sample['patches'])
            
            if len(wsi_features) > 0:
                wsi_features = torch.FloatTensor(wsi_features).unsqueeze(0).to(device)
                
                with torch.no_grad():
                    outputs, attention = model(wsi_features)
                    probabilities = F.softmax(outputs, dim=1)
                    confidence, predicted = torch.max(probabilities, 1)
                
                result = {
                    'filename': sample['filename'],
                    'true_label': sample['label'],
                    'predicted_label': predicted.item(),
                    'confidence': confidence.item(),
                    'probabilities': {
                        'Normal': probabilities[0][0].item(),
                        'Tumor': probabilities[0][1].item()
                    }
                }
                
                test_results.append(result)
                
                print(f"Sample: {sample['filename']}")
                print(f"  True: {'Tumor' if sample['label'] else 'Normal'}")
                print(f"  Predicted: {'Tumor' if predicted.item() else 'Normal'}")
                print(f"  Confidence: {confidence.item():.3f}")
                print()
        
        except Exception as e:
            print(f"Error testing sample {i}: {e}")
    
    # Save test results
    import json
    with open('/kaggle/working/test_results.json', 'w') as f:
        json.dump(test_results, f, indent=2)
    
    print(f"✓ Test results saved to /kaggle/working/test_results.json")

print("\n✅ All done! Check /kaggle/working/ for saved files:")
print("- best_focus_model.pth (trained model)")
print("- test_results.json (test predictions)")
print("\nYou can download these files to use locally!")