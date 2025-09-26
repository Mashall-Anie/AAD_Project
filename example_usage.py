#!/usr/bin/env python3
"""
Example usage scripts for FOCUS implementation
"""

import os
import sys
from pathlib import Path

def kaggle_training_example():
    """
    Example of how to run training on Kaggle
    """
    print("="*60)
    print("KAGGLE TRAINING EXAMPLE")
    print("="*60)
    
    kaggle_code = '''
# Create new Kaggle notebook with these cells:

# Cell 1: Install dependencies
!pip install openslide-python

# Cell 2: Import and setup
import sys
sys.path.append('/kaggle/input/focus-code')
from kaggle_focus_implementation import *

# Cell 3: Training
def main():
    # Kaggle dataset paths
    DATA_DIR = "/kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset"
    OUTPUT_DIR = "/kaggle/working"
    
    print("Starting FOCUS training...")
    
    # Train model with MapReduce feature extraction
    model, best_acc = train_focus_model(
        data_dir=DATA_DIR,
        output_dir=OUTPUT_DIR,
        num_epochs=30
    )
    
    print(f"Training completed! Best accuracy: {best_acc:.2f}%")

# Cell 4: Run training
if __name__ == "__main__":
    main()
'''
    
    print("Kaggle Notebook Code:")
    print(kaggle_code)
    
    print("\nSteps:")
    print("1. Create new Kaggle notebook")
    print("2. Add CAMELYON dataset and FOCUS code as data sources")
    print("3. Copy the code above into cells")
    print("4. Run the notebook")
    print("5. Download trained model from /kaggle/working/")

def local_inference_examples():
    """
    Examples of local inference usage
    """
    print("\n" + "="*60)
    print("LOCAL INFERENCE EXAMPLES")
    print("="*60)
    
    examples = [
        {
            "name": "Single Image Prediction",
            "command": "python local_deployment.py --model models/best_focus_model.pth --image sample.jpg",
            "description": "Predict a single image"
        },
        {
            "name": "Batch Prediction",
            "command": "python local_deployment.py --model models/best_focus_model.pth --batch /path/to/images/ --output results.json",
            "description": "Predict multiple images in a directory"
        },
        {
            "name": "With Attention Weights",
            "command": "python local_deployment.py --model models/best_focus_model.pth --image sample.jpg --attention --output result.json",
            "description": "Get attention visualization data"
        },
        {
            "name": "Custom Configuration",
            "command": "python local_deployment.py --model models/best_focus_model.pth --image sample.jpg --config configs/custom.json",
            "description": "Use custom processing parameters"
        }
    ]
    
    for i, example in enumerate(examples, 1):
        print(f"\n{i}. {example['name']}")
        print(f"   Description: {example['description']}")
        print(f"   Command: {example['command']}")

def fedora_setup_example():
    """
    Example setup process for Fedora
    """
    print("\n" + "="*60)
    print("FEDORA SETUP EXAMPLE")
    print("="*60)
    
    setup_commands = [
        "# 1. Install system dependencies",
        "sudo dnf update",
        "sudo dnf install python3-pip python3-opencv openslide-devel openslide-tools",
        "",
        "# 2. Clone or download FOCUS code",
        "git clone <your-repo> focus-camelyon",
        "cd focus-camelyon",
        "",
        "# 3. Install Python dependencies", 
        "pip install -r requirements.txt",
        "",
        "# 4. Run environment setup",
        "python setup_environment.py",
        "",
        "# 5. Download trained model from Kaggle",
        "# Place it in models/best_focus_model.pth",
        "",
        "# 6. Test inference",
        "python local_deployment.py --model models/best_focus_model.pth --image test.jpg"
    ]
    
    for cmd in setup_commands:
        print(cmd)

def performance_optimization_tips():
    """
    Performance optimization tips
    """
    print("\n" + "="*60)
    print("PERFORMANCE OPTIMIZATION TIPS")
    print("="*60)
    
    tips = [
        {
            "category": "CPU Optimization",
            "tips": [
                "Set OMP_NUM_THREADS to number of cores",
                "Use torch.set_num_threads(cpu_count())",
                "Prefer EfficientNet over ResNet for CPU",
                "Use ThreadPoolExecutor for I/O bound tasks"
            ]
        },
        {
            "category": "Memory Management",
            "tips": [
                "Reduce max_patches for large images",
                "Use smaller batch sizes",
                "Enable garbage collection between predictions",
                "Use torch.no_grad() for inference"
            ]
        },
        {
            "category": "MapReduce Tuning",
            "tips": [
                "Adjust num_workers based on CPU cores",
                "Use smaller patch batches for memory efficiency",
                "Monitor CPU utilization and adjust accordingly",
                "Consider process vs thread pools based on workload"
            ]
        },
        {
            "category": "Model Optimization",
            "tips": [
                "Use model.eval() for inference",
                "Consider model quantization for speed",
                "Cache feature extractors",
                "Use JIT compilation for repeated operations"
            ]
        }
    ]
    
    for tip_group in tips:
        print(f"\n{tip_group['category']}:")
        for tip in tip_group['tips']:
            print(f"  • {tip}")

def create_test_script():
    """
    Create a test script to verify installation
    """
    test_script = '''#!/usr/bin/env python3
"""
Test script to verify FOCUS installation
"""

import sys
import torch
import numpy as np
from pathlib import Path

def test_imports():
    """Test if all required packages can be imported"""
    try:
        import cv2
        import PIL
        import pandas as pd
        import sklearn
        print("✓ All required packages imported successfully")
        return True
    except ImportError as e:
        print(f"✗ Import error: {e}")
        return False

def test_torch():
    """Test PyTorch functionality"""
    try:
        x = torch.randn(10, 10)
        y = torch.mm(x, x.t())
        print(f"✓ PyTorch working (device: {torch.device('cpu')})")
        return True
    except Exception as e:
        print(f"✗ PyTorch error: {e}")
        return False

def test_opencv():
    """Test OpenCV functionality"""
    try:
        import cv2
        # Create test image
        img = np.zeros((100, 100, 3), dtype=np.uint8)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        print("✓ OpenCV working")
        return True
    except Exception as e:
        print(f"✗ OpenCV error: {e}")
        return False

def test_multiprocessing():
    """Test multiprocessing"""
    try:
        import multiprocessing as mp
        from concurrent.futures import ThreadPoolExecutor
        
        def dummy_task(x):
            return x * 2
        
        with ThreadPoolExecutor(max_workers=2) as executor:
            results = list(executor.map(dummy_task, [1, 2, 3]))
        
        print(f"✓ Multiprocessing working (CPU cores: {mp.cpu_count()})")
        return True
    except Exception as e:
        print(f"✗ Multiprocessing error: {e}")
        return False

def main():
    """Run all tests"""
    print("FOCUS Installation Test")
    print("="*30)
    
    tests = [
        test_imports,
        test_torch,
        test_opencv,
        test_multiprocessing
    ]
    
    passed = 0
    for test in tests:
        if test():
            passed += 1
    
    print(f"\\nResults: {passed}/{len(tests)} tests passed")
    
    if passed == len(tests):
        print("✓ Installation verified successfully!")
        print("You can now run FOCUS inference.")
    else:
        print("✗ Some tests failed. Check installation.")
        return 1
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
'''
    
    with open('/workspace/test_installation.py', 'w') as f:
        f.write(test_script)
    
    os.chmod('/workspace/test_installation.py', 0o755)
    print("Created test_installation.py")

def main():
    """
    Main function to show all examples
    """
    print("FOCUS Implementation Examples and Usage")
    print("="*80)
    
    kaggle_training_example()
    local_inference_examples()
    fedora_setup_example()
    performance_optimization_tips()
    
    print("\n" + "="*60)
    print("ADDITIONAL RESOURCES")
    print("="*60)
    
    create_test_script()
    
    print("\nNext steps:")
    print("1. Run: python test_installation.py")
    print("2. For Kaggle: Follow training example above")
    print("3. For Local: Follow Fedora setup example")
    print("4. Check README.md for detailed documentation")

if __name__ == "__main__":
    main()