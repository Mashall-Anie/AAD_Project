#!/bin/bash
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
