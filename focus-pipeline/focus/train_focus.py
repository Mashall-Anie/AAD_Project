import argparse
import json
import os
from typing import Dict, List, Tuple

import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
from sklearn.metrics import roc_auc_score, accuracy_score, f1_score
from tqdm import tqdm

from .mil_models import ABMIL, TransformerMIL


def load_split(metadata_dir: str) -> Tuple[List[str], List[str]]:
    import pandas as pd

    train = pd.read_csv(os.path.join(metadata_dir, "train_split.csv"))
    val = pd.read_csv(os.path.join(metadata_dir, "val_split.csv"))
    # Expect columns: slide_id, label
    return (
        list(train["slide_id"].astype(str)),
        list(val["slide_id"].astype(str)),
    )


def load_labels(metadata_dir: str) -> Dict[str, int]:
    import pandas as pd
    df = pd.read_csv(os.path.join(metadata_dir, "simple_labels.csv"))
    # Expect columns: slide_id, label in {0,1}
    return {str(r.slide_id): int(r.label) for r in df.itertuples(index=False)}


def load_bag(features_root: str, slide_id: str) -> Tuple[torch.Tensor, List[int]]:
    feat_path = os.path.join(features_root, slide_id, "features.pt")
    obj = torch.load(feat_path, map_location="cpu")
    feats: torch.Tensor = obj["features"].float()
    kept_mask: List[int] = obj.get("kept_mask", [1] * feats.shape[0])
    if len(kept_mask) == feats.shape[0]:
        feats = feats[torch.tensor(kept_mask, dtype=torch.bool)]
    return feats, kept_mask


def train_epoch(model, optimizer, criterion, train_ids, labels, features_root, device):
    model.train()
    losses = []
    for sid in tqdm(train_ids, desc="Train"):
        x, _ = load_bag(features_root, sid)
        if x.numel() == 0:
            continue
        x = x.to(device)
        y = torch.tensor([labels[sid]], dtype=torch.long, device=device)
        logits, _ = model(x)
        loss = criterion(logits.unsqueeze(0), y)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        losses.append(loss.item())
    return float(np.mean(losses)) if losses else 0.0


@torch.no_grad()
def evaluate(model, ids, labels, features_root, device):
    model.eval()
    y_true: List[int] = []
    y_prob: List[float] = []
    y_pred: List[int] = []
    for sid in tqdm(ids, desc="Eval"):
        x, _ = load_bag(features_root, sid)
        if x.numel() == 0:
            continue
        x = x.to(device)
        logits, _ = model(x)
        prob = torch.softmax(logits, dim=-1)[1].item()
        pred = int(prob >= 0.5)
        y_true.append(labels[sid])
        y_prob.append(prob)
        y_pred.append(pred)
    auc = roc_auc_score(y_true, y_prob) if len(set(y_true)) > 1 else float("nan")
    acc = accuracy_score(y_true, y_pred) if y_true else 0.0
    f1 = f1_score(y_true, y_pred) if len(set(y_true)) > 1 else float("nan")
    return {"AUC": auc, "ACC": acc, "F1": f1}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--features_dir", required=True)
    parser.add_argument("--metadata_dir", required=True)
    parser.add_argument("--results_dir", required=True)
    parser.add_argument("--epochs", type=int, default=10)
    parser.add_argument("--lr", type=float, default=1e-4)
    parser.add_argument("--batch_size", type=int, default=1)
    parser.add_argument("--model", choices=["abmil", "transformer"], default="abmil")
    args = parser.parse_args()

    os.makedirs(args.results_dir, exist_ok=True)
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    train_ids, val_ids = load_split(args.metadata_dir)
    labels = load_labels(args.metadata_dir)

    # Infer feature dim from first bag
    dim = None
    for sid in train_ids + val_ids:
        try:
            x, _ = load_bag(args.features_dir, sid)
            if x.numel() > 0:
                dim = x.shape[1]
                break
        except FileNotFoundError:
            continue
    if dim is None:
        raise RuntimeError("No features found to infer dimension.")

    if args.model == "abmil":
        model = ABMIL(in_dim=dim, hidden_dim=256, num_classes=2)
    else:
        model = TransformerMIL(in_dim=dim, embed_dim=256, depth=2, num_heads=8, num_classes=2)
    model = model.to(device)

    optimizer = optim.Adam(model.parameters(), lr=args.lr)
    criterion = nn.CrossEntropyLoss()

    history = []
    for epoch in range(args.epochs):
        loss = train_epoch(model, optimizer, criterion, train_ids, labels, args.features_dir, device)
        metrics = evaluate(model, val_ids, labels, args.features_dir, device)
        row = {"epoch": epoch, "loss": loss, **metrics}
        history.append(row)
        print(row)

    with open(os.path.join(args.results_dir, "metrics.json"), "w") as f:
        json.dump(history, f, indent=2)

    torch.save(model.state_dict(), os.path.join(args.results_dir, "checkpoints_abmil.pt" if args.model=="abmil" else "checkpoints_trans.pt"))


if __name__ == "__main__":
    main()

