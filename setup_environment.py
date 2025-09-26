#!/usr/bin/env python3
"""
Environment setup script for FOCUS deployment
Works on both Kaggle and local Fedora systems
"""

import subprocess
import sys
import os
from pathlib import Path

def install_package(package):
    """Install package using pip"""
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])

def setup_kaggle_environment():
    """Setup packages for Kaggle environment"""
    print("Setting up Kaggle environment...")
    
    packages = [
        "openslide-python",
        "opencv-python",
        "pillow",
        "pandas",
        "numpy",
        "torch",
        "torchvision",
        "scikit-learn",
        "matplotlib",
        "seaborn",
        "tqdm"
    ]
    
    for package in packages:
        try:
            print(f"Installing {package}...")
            install_package(package)
        except Exception as e:
            print(f"Warning: Could not install {package}: {e}")

def setup_fedora_environment():
    """Setup packages for Fedora local environment"""
    print("Setting up Fedora environment...")
    
    # System packages (requires sudo)
    system_packages = [
        "python3-pip",
        "python3-opencv", 
        "openslide-devel",
        "openslide-tools"
    ]
    
    print("Installing system packages (requires sudo):")
    for package in system_packages:
        try:
            subprocess.run(["sudo", "dnf", "install", "-y", package], check=True)
        except Exception as e:
            print(f"Warning: Could not install {package}: {e}")
    
    # Python packages
    python_packages = [
        "openslide-python",
        "opencv-python",
        "pillow",
        "pandas",
        "numpy",
        "torch",
        "torchvision", 
        "scikit-learn",
        "matplotlib",
        "seaborn",
        "tqdm"
    ]
    
    for package in python_packages:
        try:
            print(f"Installing {package}...")
            install_package(package)
        except Exception as e:
            print(f"Warning: Could not install {package}: {e}")

def create_project_structure():
    """Create project directory structure"""
    directories = [
        "models",
        "data",
        "results", 
        "logs",
        "configs"
    ]
    
    for directory in directories:
        Path(directory).mkdir(exist_ok=True)
        print(f"Created directory: {directory}")

def create_config_files():
    """Create configuration files"""
    
    # Training config
    train_config = {
        "model": {
            "feature_dim": 512,
            "num_classes": 2,
            "num_heads": 8,
            "compress_dim": 512
        },
        "training": {
            "num_epochs": 30,
            "learning_rate": 1e-4,
            "weight_decay": 1e-5,
            "batch_size": 1
        },
        "data": {
            "patch_size": 224,
            "overlap": 0.5,
            "max_patches": 500,
            "train_split": 0.8
        },
        "mapreduce": {
            "num_workers": 4,
            "batch_size": 50
        }
    }
    
    import json
    with open("configs/train_config.json", "w") as f:
        json.dump(train_config, f, indent=2)
    
    # Inference config
    inference_config = {
        "model": {
            "feature_dim": 512,
            "num_classes": 2
        },
        "processing": {
            "patch_size": 224,
            "overlap": 0.3,
            "max_patches": 200
        },
        "mapreduce": {
            "num_workers": 4
        },
        "classes": ["Normal", "Tumor"]
    }
    
    with open("configs/inference_config.json", "w") as f:
        json.dump(inference_config, f, indent=2)
    
    print("Created configuration files")

def create_scripts():
    """Create utility scripts"""
    
    # Kaggle training script
    kaggle_script = '''#!/bin/bash
# Kaggle training script
python /kaggle/input/focus-code/kaggle_focus_implementation.py
'''
    
    with open("train_kaggle.sh", "w") as f:
        f.write(kaggle_script)
    os.chmod("train_kaggle.sh", 0o755)
    
    # Local inference script
    local_script = '''#!/bin/bash
# Local inference script
python local_deployment.py --model models/best_focus_model.pth --image "$1" --output results/prediction.json
'''
    
    with open("predict_local.sh", "w") as f:
        f.write(local_script)
    os.chmod("predict_local.sh", 0o755)
    
    print("Created utility scripts")

def main():
    """Main setup function"""
    print("FOCUS Environment Setup")
    print("="*50)
    
    # Detect environment
    if '/kaggle/' in os.getcwd():
        setup_kaggle_environment()
    else:
        # Assume Fedora local environment
        setup_fedora_environment()
    
    # Create project structure
    create_project_structure()
    create_config_files()
    create_scripts()
    
    print("\nSetup completed!")
    print("\nNext steps:")
    print("1. For Kaggle: Upload this code and run kaggle_focus_implementation.py")
    print("2. For Local: Download trained model and use local_deployment.py")
    print("\nExample usage:")
    print("# Training on Kaggle:")
    print("python kaggle_focus_implementation.py")
    print("\n# Inference on local:")
    print("python local_deployment.py --model models/best_focus_model.pth --image sample.jpg")

if __name__ == "__main__":
    main()