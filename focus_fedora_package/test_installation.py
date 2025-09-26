#!/usr/bin/env python3
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
    
    print("\n1. Testing system commands...")
    sys_ok = test_system_commands()
    
    print("\n2. Testing Python packages...")
    pkg_ok = test_python_packages()
    
    print("\n" + "="*40)
    if sys_ok and pkg_ok:
        print("🎉 All tests passed! FOCUS is ready to use.")
        print("\nNext: Download model and run:")
        print("python3 local_deployment.py --model models/best_focus_model.pth --image test.jpg")
        return 0
    else:
        print("❌ Some tests failed. Run setup_fedora.sh again.")
        return 1

if __name__ == "__main__":
    sys.exit(main())
