## FOCUS Camelyon Pipeline (Kaggle + Local CPU)

This repo provides a lightweight, Kaggle-ready pipeline to:

- Tile Camelyon WSIs and extract patch embeddings via CONCH (with `open_clip` fallback)
- Apply a FOCUS-style knowledge-guided adaptive selection (MapReduce over patches)
- Train a MIL aggregator on few-shot splits and evaluate (AUC/ACC/F1)
- Compare against simple baselines (Transformer MIL ≈ TransMIL-lite, ABMIL ≈ DSMIL-lite)
- Run CPU-only local inference on Fedora for a single WSI

### Directory layout

- `focus/tiling.py`: WSI tiling utilities using OpenSlide
- `focus/conch_wrapper.py`: Embedding wrapper for CONCH or open_clip fallback
- `focus/knowledge_selector.py`: Knowledge-guided patch scoring/selection
- `focus/mapreduce_extract.py`: End-to-end MapReduce extraction to per-WSI features
- `focus/mil_models.py`: MIL aggregators (FOCUS-ABMIL, Transformer MIL)
- `focus/train_focus.py`: Train/evaluate FOCUS and baselines; export metrics
- `focus/eval_metrics.py`: Metric utilities and table export
- `focus/local_infer.py`: CPU inference for a single WSI
- `focus/baselines/prepare_baselines.py`: Optional export to common feature formats

### Requirements

Install dependencies (Kaggle or local):

```bash
pip install -r requirements.txt
# Fedora/Local: OpenSlide runtime
sudo dnf install -y openslide openslide-tools || true
```

If using CONCH from Kaggle Inputs, point to the paths:

- CONCH code: `/kaggle/input/conch-code/CONCH-main`
- CONCH weights: `/kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset/models/conch.pth`

### Kaggle usage (Camelyon subset)

1) Extract features with MapReduce + FOCUS selection (adjust paths as needed):

```bash
python -m focus.mapreduce_extract \
  --wsi_root /kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset/raw_data/wsi_images \
  --metadata_dir /kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset/metadata \
  --output_root /kaggle/working/focus_outputs \
  --conch_repo /kaggle/input/conch-code/CONCH-main \
  --conch_ckpt /kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset/models/conch.pth \
  --tile_size 256 --stride 256 --level 0 --num_workers 2 --batch_size 64
```

2) Train FOCUS MIL and baselines; export tables:

```bash
python -m focus.train_focus \
  --features_dir /kaggle/working/focus_outputs/features \
  --metadata_dir /kaggle/input/camelyon-focus-dataset/camelyon-focus-dataset/metadata \
  --results_dir /kaggle/working/focus_results \
  --epochs 10 --lr 1e-4 --batch_size 1
```

Artifacts:

- Metrics CSVs and concise Table 1/2 in `results_dir`
- Trained MIL checkpoints in `results_dir/checkpoints`

### Local CPU inference (Fedora, no GPU)

```bash
python -m focus.local_infer \
  --wsi_path /path/to/your/slide.tif \
  --checkpoint /path/to/focus_mil.ckpt \
  --conch_repo /path/to/CONCH-main \
  --conch_ckpt /path/to/conch.pth \
  --out_json ./prediction.json
```

Notes:

- If CONCH is unavailable, the wrapper falls back to `open_clip` ViT-B-16. Accuracy may drop.
- For TransMIL/DSMIL official repos, use `focus/baselines/prepare_baselines.py` to export features, then follow their README to train on features.

