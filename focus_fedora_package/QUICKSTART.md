# FOCUS Quick Start Guide for Fedora

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
