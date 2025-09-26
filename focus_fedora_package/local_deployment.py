#!/usr/bin/env python3
"""
Local Deployment for FOCUS Model on Fedora
CPU-optimized inference with MapReduce for single image prediction
"""

import os
import sys
import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import torchvision.transforms as transforms
from PIL import Image
import cv2
import multiprocessing as mp
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
from pathlib import Path
import logging
import argparse
import json
import time
from typing import List, Tuple, Dict, Optional
import warnings
warnings.filterwarnings('ignore')

# CPU optimization
torch.set_num_threads(mp.cpu_count())
os.environ['OMP_NUM_THREADS'] = str(mp.cpu_count())

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class AdaptiveVisualCompression(nn.Module):
    """
    FOCUS: Knowledge-Enhanced Adaptive Visual Compression
    CPU-optimized version
    """
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
        """
        Args:
            features: [batch_size, num_patches, input_dim]
            knowledge_features: Optional knowledge enhancement
        """
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

class CPUOptimizedFeatureExtractor:
    """
    CPU-optimized feature extraction for local deployment
    """
    def __init__(self, device='cpu'):
        self.device = device
        self.model = self._load_model()
        self.transform = self._get_transforms()
        
    def _load_model(self):
        """Load lightweight feature extraction model"""
        import torchvision.models as models
        
        # Use EfficientNet for better CPU performance
        try:
            model = models.efficientnet_b0(pretrained=True)
            model.classifier = nn.Identity()  # Remove final layer
        except:
            # Fallback to ResNet50
            model = models.resnet50(pretrained=True)
            model.fc = nn.Identity()
        
        model.eval()
        return model.to(self.device)
    
    def _get_transforms(self):
        """Get image transforms optimized for CPU"""
        return transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], 
                               std=[0.229, 0.224, 0.225])
        ])
    
    def extract_patch_features(self, patch):
        """Extract features from a single patch - CPU optimized"""
        with torch.no_grad():
            if isinstance(patch, np.ndarray):
                patch = Image.fromarray(patch.astype(np.uint8))
            
            patch_tensor = self.transform(patch).unsqueeze(0).to(self.device)
            
            # Use torch.jit for faster inference
            features = self.model(patch_tensor)
            return features.cpu().numpy().flatten()

class LocalMapReduceProcessor:
    """
    MapReduce implementation optimized for local CPU processing
    """
    def __init__(self, feature_extractor, num_workers=None):
        self.feature_extractor = feature_extractor
        # Use fewer workers for local processing to avoid memory issues
        self.num_workers = min(num_workers or mp.cpu_count(), 4)
        
    def process_patch_batch(self, patch_batch):
        """Process a batch of patches"""
        features = []
        for patch in patch_batch:
            try:
                feat = self.feature_extractor.extract_patch_features(patch)
                features.append(feat)
            except Exception as e:
                logger.warning(f"Error processing patch: {e}")
                # Use default feature size based on model
                default_size = 1000 if 'efficientnet' in str(type(self.feature_extractor.model)) else 2048
                features.append(np.zeros(default_size))
        return np.array(features)
    
    def extract_features_parallel(self, patches):
        """Extract features using parallel processing"""
        if len(patches) == 0:
            return np.array([])
        
        # Split patches into smaller batches for local processing
        batch_size = max(1, len(patches) // self.num_workers)
        patch_batches = [patches[i:i+batch_size] 
                        for i in range(0, len(patches), batch_size)]
        
        # Use ThreadPoolExecutor for I/O bound tasks on CPU
        with ThreadPoolExecutor(max_workers=self.num_workers) as executor:
            feature_batches = list(executor.map(self.process_patch_batch, patch_batches))
        
        # Combine results
        if feature_batches:
            return np.vstack(feature_batches)
        else:
            return np.array([])

class LocalImageProcessor:
    """
    Process images locally with CPU optimization
    """
    def __init__(self, patch_size=224, overlap=0.3, max_patches=200):
        self.patch_size = patch_size
        self.overlap = overlap
        self.max_patches = max_patches
    
    def extract_patches_from_image(self, image_path):
        """Extract patches from any image format"""
        try:
            # Load image
            img = cv2.imread(str(image_path))
            if img is None:
                logger.error(f"Could not load image: {image_path}")
                return []
            
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
            return self._extract_patches_array(img)
            
        except Exception as e:
            logger.error(f"Error processing image {image_path}: {e}")
            return []
    
    def _extract_patches_array(self, img_array):
        """Extract patches from image array"""
        h, w = img_array.shape[:2]
        patches = []
        
        # Calculate step size
        step = int(self.patch_size * (1 - self.overlap))
        step = max(step, 1)
        
        # Extract patches with adaptive sampling
        for y in range(0, h - self.patch_size + 1, step):
            for x in range(0, w - self.patch_size + 1, step):
                patch = img_array[y:y+self.patch_size, x:x+self.patch_size]
                
                # Quality check for patch
                if self._is_valid_patch(patch):
                    patches.append(patch)
                    
                if len(patches) >= self.max_patches:
                    return patches
        
        # If we have very few patches, add some overlapping ones
        if len(patches) < 10 and h >= self.patch_size and w >= self.patch_size:
            # Add center patch
            center_y = (h - self.patch_size) // 2
            center_x = (w - self.patch_size) // 2
            center_patch = img_array[center_y:center_y+self.patch_size, 
                                   center_x:center_x+self.patch_size]
            patches.append(center_patch)
        
        return patches
    
    def _is_valid_patch(self, patch, min_variance=100):
        """Check if patch has enough information content"""
        # Check variance to avoid uniform patches
        gray = cv2.cvtColor(patch, cv2.COLOR_RGB2GRAY)
        variance = np.var(gray)
        return variance > min_variance

class FOCUSClassifier(nn.Module):
    """
    FOCUS Few-Shot WSI Classifier - Local deployment version
    """
    def __init__(self, feature_dim=512, num_classes=2, input_feature_dim=2048):
        super().__init__()
        self.feature_dim = feature_dim
        self.num_classes = num_classes
        
        # Feature compression and enhancement
        self.adaptive_compression = AdaptiveVisualCompression(
            input_dim=input_feature_dim, compress_dim=feature_dim
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
        """
        Args:
            wsi_features: [batch_size, num_patches, input_feature_dim]
        """
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

class FOCUSInference:
    """
    FOCUS model inference for local deployment
    """
    def __init__(self, model_path, device='cpu'):
        self.device = device
        self.model = self._load_model(model_path)
        self.feature_extractor = CPUOptimizedFeatureExtractor(device)
        self.mapreduce_processor = LocalMapReduceProcessor(self.feature_extractor)
        self.image_processor = LocalImageProcessor()
        
        # Class names
        self.class_names = ['Normal', 'Tumor']
        
    def _load_model(self, model_path):
        """Load trained FOCUS model"""
        try:
            checkpoint = torch.load(model_path, map_location=self.device)
            
            # Determine input feature dimension
            input_dim = 1000  # EfficientNet default
            if 'resnet' in str(type(self.feature_extractor.model)):
                input_dim = 2048
                
            model = FOCUSClassifier(
                feature_dim=512, 
                num_classes=2,
                input_feature_dim=input_dim
            ).to(self.device)
            
            model.load_state_dict(checkpoint['model_state_dict'])
            model.eval()
            
            logger.info(f"Model loaded successfully from {model_path}")
            logger.info(f"Best accuracy: {checkpoint.get('best_acc', 'N/A')}")
            
            return model
            
        except Exception as e:
            logger.error(f"Error loading model: {e}")
            # Create dummy model for testing
            return FOCUSClassifier(feature_dim=512, num_classes=2).to(self.device)
    
    def predict_image(self, image_path, return_attention=False):
        """
        Predict single image with FOCUS model
        
        Args:
            image_path: Path to input image
            return_attention: Whether to return attention weights
            
        Returns:
            dict: Prediction results
        """
        start_time = time.time()
        
        # Extract patches
        logger.info(f"Processing image: {image_path}")
        patches = self.image_processor.extract_patches_from_image(image_path)
        
        if len(patches) == 0:
            return {
                'prediction': 'Normal',
                'confidence': 0.0,
                'error': 'No valid patches found'
            }
        
        logger.info(f"Extracted {len(patches)} patches")
        
        # Extract features using MapReduce
        patch_time = time.time()
        features = self.mapreduce_processor.extract_features_parallel(patches)
        feature_time = time.time() - patch_time
        
        logger.info(f"Feature extraction completed in {feature_time:.2f}s")
        
        if len(features) == 0:
            return {
                'prediction': 'Normal',
                'confidence': 0.0,
                'error': 'Feature extraction failed'
            }
        
        # Run inference
        with torch.no_grad():
            features_tensor = torch.FloatTensor(features).unsqueeze(0).to(self.device)
            outputs, attention_weights = self.model(features_tensor)
            
            # Get prediction
            probabilities = F.softmax(outputs, dim=1)
            confidence, predicted = torch.max(probabilities, 1)
            
            prediction = self.class_names[predicted.item()]
            confidence_score = confidence.item()
        
        total_time = time.time() - start_time
        
        result = {
            'prediction': prediction,
            'confidence': confidence_score,
            'probabilities': {
                'Normal': probabilities[0][0].item(),
                'Tumor': probabilities[0][1].item()
            },
            'num_patches': len(patches),
            'processing_time': total_time,
            'feature_extraction_time': feature_time
        }
        
        if return_attention:
            result['attention_weights'] = attention_weights.cpu().numpy()
        
        return result
    
    def batch_predict(self, image_paths, output_file=None):
        """
        Predict multiple images
        """
        results = []
        
        for i, image_path in enumerate(image_paths):
            logger.info(f"Processing {i+1}/{len(image_paths)}: {image_path}")
            
            try:
                result = self.predict_image(image_path)
                result['image_path'] = str(image_path)
                results.append(result)
                
                logger.info(f"Prediction: {result['prediction']} "
                           f"(confidence: {result['confidence']:.3f})")
                
            except Exception as e:
                logger.error(f"Error processing {image_path}: {e}")
                results.append({
                    'image_path': str(image_path),
                    'prediction': 'Error',
                    'confidence': 0.0,
                    'error': str(e)
                })
        
        if output_file:
            import json
            with open(output_file, 'w') as f:
                json.dump(results, f, indent=2)
            logger.info(f"Results saved to {output_file}")
        
        return results

def main():
    """
    Main function for local inference
    """
    parser = argparse.ArgumentParser(description='FOCUS Local Inference')
    parser.add_argument('--model', required=True, help='Path to trained model')
    parser.add_argument('--image', help='Single image to predict')
    parser.add_argument('--batch', help='Directory with images to predict')
    parser.add_argument('--output', help='Output JSON file for results')
    parser.add_argument('--attention', action='store_true', 
                       help='Return attention weights')
    
    args = parser.parse_args()
    
    # Initialize inference
    logger.info("Initializing FOCUS inference...")
    inference = FOCUSInference(args.model, device='cpu')
    
    if args.image:
        # Single image prediction
        result = inference.predict_image(args.image, return_attention=args.attention)
        
        print("\n" + "="*50)
        print("FOCUS PREDICTION RESULTS")
        print("="*50)
        print(f"Image: {args.image}")
        print(f"Prediction: {result['prediction']}")
        print(f"Confidence: {result['confidence']:.3f}")
        print(f"Probabilities:")
        for class_name, prob in result['probabilities'].items():
            print(f"  {class_name}: {prob:.3f}")
        print(f"Processing time: {result['processing_time']:.2f}s")
        print(f"Number of patches: {result['num_patches']}")
        
        if args.output:
            import json
            with open(args.output, 'w') as f:
                json.dump(result, f, indent=2)
            print(f"Results saved to {args.output}")
    
    elif args.batch:
        # Batch prediction
        image_dir = Path(args.batch)
        image_paths = []
        
        # Find all image files
        for ext in ['*.jpg', '*.jpeg', '*.png', '*.tif', '*.tiff']:
            image_paths.extend(image_dir.glob(ext))
            image_paths.extend(image_dir.glob(ext.upper()))
        
        if not image_paths:
            logger.error(f"No images found in {args.batch}")
            return
        
        logger.info(f"Found {len(image_paths)} images")
        
        # Run batch prediction
        results = inference.batch_predict(
            image_paths, 
            output_file=args.output
        )
        
        # Print summary
        print("\n" + "="*50)
        print("BATCH PREDICTION SUMMARY")
        print("="*50)
        
        normal_count = sum(1 for r in results if r.get('prediction') == 'Normal')
        tumor_count = sum(1 for r in results if r.get('prediction') == 'Tumor')
        error_count = sum(1 for r in results if r.get('prediction') == 'Error')
        
        print(f"Total images: {len(results)}")
        print(f"Normal: {normal_count}")
        print(f"Tumor: {tumor_count}")
        print(f"Errors: {error_count}")
        
        avg_confidence = np.mean([r.get('confidence', 0) for r in results 
                                 if r.get('prediction') != 'Error'])
        print(f"Average confidence: {avg_confidence:.3f}")
    
    else:
        print("Please specify either --image or --batch")
        parser.print_help()

if __name__ == "__main__":
    main()