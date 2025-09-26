#!/usr/bin/env python3
"""
Script to create downloadable package with all necessary files
Run this to get all files for local Fedora deployment
"""

import os
import shutil
from pathlib import Path

def create_focus_package():
    """Create complete package for local deployment"""
    
    package_dir = Path("/workspace/focus_fedora_package")
    package_dir.mkdir(exist_ok=True)
    
    print("Creating FOCUS package for Fedora...")
    print("="*50)
    
    # Files to include
    files_to_copy = [
        "setup_environment.py",
        "local_deployment.py", 
        "requirements.txt",
        "README.md",
        "example_usage.py"
    ]
    
    # Copy files
    for file_name in files_to_copy:
        src = Path("/workspace") / file_name
        dst = package_dir / file_name
        
        if src.exists():
            shutil.copy2(src, dst)
            print(f"✓ Copied {file_name}")
        else:
            print(f"✗ Missing {file_name}")
    
    # Create additional required files
    create_fedora_setup_script(package_dir)
    create_test_script(package_dir)
    create_quick_start_guide(package_dir)
    
    print(f"\n✅ Package created at: {package_dir}")
    print(f"📦 Total files: {len(list(package_dir.glob('*')))}")
    
    return package_dir

def create_fedora_setup_script(package_dir):
    """Create Fedora-specific setup script"""
    
    fedora_setup = '''#!/bin/bash
# FOCUS Setup for Fedora - Run this first!

echo "🚀 Setting up FOCUS on Fedora..."
echo "================================="

# Update system
echo "📦 Updating system packages..."
sudo dnf update -y

# Install system dependencies
echo "📦 Installing system dependencies..."
sudo dnf install -y python3-pip python3-devel python3-opencv openslide-devel openslide-tools

# Install Python packages
echo "🐍 Installing Python packages..."
pip3 install --user -r requirements.txt

# Create directory structure
echo "📁 Creating directories..."
mkdir -p models data results logs configs

# Set executable permissions
chmod +x *.py
chmod +x *.sh

# Test installation
echo "🧪 Testing installation..."
python3 test_installation.py

echo ""
echo "✅ Setup completed!"
echo ""
echo "Next steps:"
echo "1. Download trained model from Kaggle to models/"
echo "2. Test with: python3 local_deployment.py --model models/best_focus_model.pth --image your_image.jpg"
echo ""
'''
    
    with open(package_dir / "setup_fedora.sh", "w") as f:
        f.write(fedora_setup)
    
    os.chmod(package_dir / "setup_fedora.sh", 0o755)
    print("✓ Created setup_fedora.sh")

def create_test_script(package_dir):
    """Create installation test script"""
    
    test_script = '''#!/usr/bin/env python3
"""
Test FOCUS installation on Fedora
"""

import sys
import subprocess

def test_python_packages():
    """Test Python package imports"""
    required_packages = [
        'torch', 'torchvision', 'numpy', 'pandas', 
        'cv2', 'PIL', 'sklearn', 'matplotlib'
    ]
    
    failed = []
    
    for package in required_packages:
        try:
            if package == 'cv2':
                import cv2
            elif package == 'PIL':
                import PIL
            elif package == 'sklearn':
                import sklearn
            else:
                __import__(package)
            print(f"✓ {package}")
        except ImportError:
            print(f"✗ {package}")
            failed.append(package)
    
    return len(failed) == 0

def test_system_commands():
    """Test system tools"""
    commands = ['python3', 'pip3']
    
    for cmd in commands:
        try:
            result = subprocess.run([cmd, '--version'], 
                                  capture_output=True, text=True)
            if result.returncode == 0:
                print(f"✓ {cmd}: {result.stdout.strip()}")
            else:
                print(f"✗ {cmd}: not working")
                return False
        except FileNotFoundError:
            print(f"✗ {cmd}: not found")
            return False
    
    return True

def main():
    print("FOCUS Installation Test for Fedora")
    print("="*40)
    
    print("\\n1. Testing system commands...")
    sys_ok = test_system_commands()
    
    print("\\n2. Testing Python packages...")
    pkg_ok = test_python_packages()
    
    print("\\n" + "="*40)
    if sys_ok and pkg_ok:
        print("🎉 All tests passed! FOCUS is ready to use.")
        print("\\nNext: Download model and run:")
        print("python3 local_deployment.py --model models/best_focus_model.pth --image test.jpg")
        return 0
    else:
        print("❌ Some tests failed. Run setup_fedora.sh again.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
'''
    
    with open(package_dir / "test_installation.py", "w") as f:
        f.write(test_script)
    
    os.chmod(package_dir / "test_installation.py", 0o755)
    print("✓ Created test_installation.py")

def create_quick_start_guide(package_dir):
    """Create quick start guide"""
    
    guide = '''# FOCUS Quick Start Guide for Fedora

## 🚀 Setup (First Time)

1. **Extract files** to a directory:
   ```bash
   mkdir focus-local
   cd focus-local
   # Copy all files here
   ```

2. **Run setup script**:
   ```bash
   chmod +x setup_fedora.sh
   ./setup_fedora.sh
   ```

3. **Test installation**:
   ```bash
   python3 test_installation.py
   ```

## 📥 Get Trained Model

1. **Download from Kaggle**:
   - After training on Kaggle
   - Download `best_focus_model.pth`
   - Place in `models/` directory

## 🔮 Run Inference

### Single Image:
```bash
python3 local_deployment.py --model models/best_focus_model.pth --image sample.jpg
```

### Batch Images:
```bash
python3 local_deployment.py --model models/best_focus_model.pth --batch /path/to/images/ --output results.json
```

### With Attention:
```bash
python3 local_deployment.py --model models/best_focus_model.pth --image sample.jpg --attention --output result.json
```

## 📁 Directory Structure

```
focus-local/
├── setup_fedora.sh          # Setup script
├── local_deployment.py      # Main inference script
├── setup_environment.py     # Environment setup
├── test_installation.py     # Test script
├── requirements.txt         # Dependencies
├── models/                  # Put trained model here
│   └── best_focus_model.pth
├── data/                    # Test images
├── results/                 # Prediction outputs
└── configs/                 # Configuration files
```

## 🔧 Troubleshooting

### Common Issues:

1. **Missing OpenSlide**:
   ```bash
   sudo dnf install openslide-devel openslide-tools
   ```

2. **PyTorch CPU version**:
   ```bash
   pip3 install torch torchvision --index-url https://download.pytorch.org/whl/cpu
   ```

3. **Permission errors**:
   ```bash
   chmod +x *.py *.sh
   ```

4. **Memory issues**:
   - Reduce `max_patches` in config
   - Use fewer `num_workers`

## ⚡ Performance Tips

- **CPU cores**: Uses all available CPU cores
- **Memory**: ~2-4GB RAM per inference
- **Speed**: ~5-10 seconds per image
- **Optimization**: Use EfficientNet for faster CPU inference

## 📞 Support

Check README.md for detailed documentation and troubleshooting.
'''
    
    with open(package_dir / "QUICKSTART.md", "w") as f:
        f.write(guide)
    
    print("✓ Created QUICKSTART.md")

def main():
    """Create complete package"""
    package_dir = create_focus_package()
    
    print("\n" + "="*60)
    print("📦 FOCUS FEDORA PACKAGE CREATED")
    print("="*60)
    
    print(f"\n📍 Location: {package_dir}")
    print("\n📋 Files included:")
    
    for file_path in sorted(package_dir.glob("*")):
        size = file_path.stat().st_size / 1024  # KB
        print(f"  📄 {file_path.name:<25} ({size:.1f} KB)")
    
    print(f"\n📦 Total package size: {sum(f.stat().st_size for f in package_dir.glob('*')) / 1024 / 1024:.1f} MB")
    
    print("\n🔧 Next steps:")
    print("1. Copy all files from this directory to your Fedora machine")
    print("2. Run: ./setup_fedora.sh")
    print("3. Test: python3 test_installation.py")
    print("4. Download model from Kaggle to models/")
    print("5. Run inference!")
    
    return package_dir

if __name__ == "__main__":
    main()