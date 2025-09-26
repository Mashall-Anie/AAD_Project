# FOCUS: Knowledge-Enhanced Adaptive Visual Compression for Few-Shot WSI Classification

Implementation of FOCUS framework for CAMELYON dataset with MapReduce optimization for both Kaggle training and local Fedora deployment.

## Overview

This implementation provides:
- **FOCUS Framework**: Adaptive visual compression with knowledge enhancement
- **MapReduce Feature Extraction**: Parallel processing for large WSI images
- **Few-Shot Learning**: Efficient learning with limited labeled data
- **Cross-Platform Deployment**: Kaggle training + local inference

## Project Structure

```
focus-camelyon/
├── kaggle_focus_implementation.py  # Main training script for Kaggle
├── local_deployment.py            # Local inference script for Fedora
├── setup_environment.py           # Environment setup
├── requirements.txt               # Python dependencies
├── configs/                       # Configuration files
├── models/                        # Trained models
├── data/                          # Data directory
├── results/                       # Prediction results
└── logs/                          # Training logs
```

## Setup

### 1. Environment Setup

```bash
# Install dependencies
pip install -r requirements.txt

# Run setup script
python setup_environment.py
```

### 2. For Fedora Local Setup

```bash
# Install system dependencies
sudo dnf install python3-pip python3-opencv openslide-devel openslide-tools

# Install Python packages
pip install -r requirements.txt
```

## Usage

### Training on Kaggle

1. Upload the dataset and code to Kaggle
2. Ensure dataset structure matches:
   ```
   /kaggle/input/camelyon-focus-dataset/
   ├── metadata/
   │   ├── train_split.csv
   │   ├── val_split.csv
   │   ├── reference.csv
   │   └── simple_labels.csv
   ├── raw_data/wsi_images/
   │   ├── normal/
   │   └── turnor/
   └── models/
       └── conch.pth
   ```

3. Run training:
   ```python
   python kaggle_focus_implementation.py
   ```

### Local Inference on Fedora

1. Download trained model from Kaggle
2. Place model in `models/` directory
3. Run inference:

```bash
# Single image prediction
python local_deployment.py --model models/best_focus_model.pth --image sample.jpg

# Batch prediction
python local_deployment.py --model models/best_focus_model.pth --batch /path/to/images/ --output results.json

# With attention visualization
python local_deployment.py --model models/best_focus_model.pth --image sample.jpg --attention --output result.json
```

## Features

### FOCUS Framework Components

1. **Adaptive Visual Compression**:
   - Multi-head attention mechanism
   - Feature compression with knowledge enhancement
   - Removes irrelevant background regions

2. **MapReduce Feature Extraction**:
   - Parallel patch processing
   - Distributed feature computation
   - Optimized for both GPU (Kaggle) and CPU (local)

3. **Few-Shot Learning**:
   - Prototype-based classification
   - Knowledge-enhanced features
   - Efficient with limited training data

### Performance Optimizations

- **CPU Optimization**: Multi-threading and process parallelization
- **Memory Efficiency**: Batch processing and lazy loading
- **Model Compression**: Lightweight feature extractors for local deployment

## Hardware Requirements

### Kaggle Training
- GPU enabled notebook
- 16GB+ RAM recommended
- Internet connection for model downloads

### Local Inference (Fedora)
- Intel i5 1240P (12 cores)
- 32GB RAM
- CPU-only inference
- No GPU required

## Model Architecture

```
Input WSI → Patch Extraction → MapReduce Feature Extraction → 
FOCUS Compression → Few-Shot Classification → Prediction
```

### Key Components:

1. **WSI Processor**: Extracts patches from whole slide images
2. **Feature Extractor**: CONCH/ResNet/EfficientNet based features
3. **MapReduce Engine**: Parallel feature computation
4. **FOCUS Classifier**: Adaptive compression + few-shot learning

## Configuration

Edit `configs/train_config.json` and `configs/inference_config.json` to customize:

- Model parameters (feature dimensions, attention heads)
- Training hyperparameters (learning rate, epochs)
- Data processing (patch size, overlap)
- MapReduce settings (workers, batch size)

## Expected Results

- **Training Time**: ~2-3 hours on Kaggle GPU
- **Inference Time**: ~5-10 seconds per image on local CPU
- **Memory Usage**: ~2-4GB during inference
- **Accuracy**: Expected 85-90% on CAMELYON validation set

## Troubleshooting

### Common Issues:

1. **OpenSlide Installation**: Install system packages first
2. **Memory Issues**: Reduce max_patches or num_workers
3. **CPU Performance**: Use EfficientNet instead of ResNet
4. **Model Loading**: Check model path and device compatibility

### Performance Tips:

- Use fewer patches for faster inference
- Reduce overlap for speed vs accuracy trade-off
- Adjust number of workers based on CPU cores
- Use model quantization for further speedup

## Citation

```bibtex
@inproceedings{focus2025,
  title={FOCUS: Knowledge-Enhanced Adaptive Visual Compression for Few-Shot Whole Slide Image Classification},
  author={Smart Lab, HKUST},
  booktitle={CVPR},
  year={2025}
}
```

## License

See original FOCUS repository for licensing information.