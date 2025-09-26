import argparse
import json
import os
from dataclasses import dataclass
from typing import Dict, List, Tuple

import torch
from tqdm import tqdm

from .tiling import generate_tiles
from .conch_wrapper import ConchOrClipEncoder
from .knowledge_selector import KnowledgeGuidedSelector, SimpleTextEmbedder


@dataclass
class WSIResult:
    wsi_path: str
    coords: List[Tuple[int, int]]
    features_path: str
    kept_mask: List[int]


def extract_features_for_wsi(
    wsi_path: str,
    encoder: ConchOrClipEncoder,
    tile_size: int,
    stride: int,
    level: int,
    batch_size: int,
    keep_ratio: float,
    classnames: Tuple[str, ...],
    out_dir: str,
) -> WSIResult:
    tiles = list(generate_tiles(wsi_path, level=level, tile_size=tile_size, stride=stride))
    coords = [(t.x, t.y) for t in tiles]
    pil_list = [t.image for t in tiles]
    if len(pil_list) == 0:
        os.makedirs(out_dir, exist_ok=True)
        empty_feat = os.path.join(out_dir, "features.pt")
        torch.save({"features": torch.empty(0, encoder.embed_dim)}, empty_feat)
        return WSIResult(wsi_path=wsi_path, coords=[], features_path=empty_feat, kept_mask=[])

    features = encoder.encode_pil_batch(pil_list, batch_size=batch_size)

    selector = KnowledgeGuidedSelector(
        text_embedder=SimpleTextEmbedder(device=str(encoder.device)),
        classnames=classnames,
        keep_ratio=keep_ratio,
    )
    kept_mask = selector(features).cpu().tolist()

    os.makedirs(out_dir, exist_ok=True)
    feat_path = os.path.join(out_dir, "features.pt")
    torch.save({"features": features.cpu(), "coords": coords, "kept_mask": kept_mask}, feat_path)

    return WSIResult(wsi_path=wsi_path, coords=coords, features_path=feat_path, kept_mask=kept_mask)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--wsi_root", required=True)
    parser.add_argument("--metadata_dir", required=True)
    parser.add_argument("--output_root", required=True)
    parser.add_argument("--conch_repo", default=None)
    parser.add_argument("--conch_ckpt", default=None)
    parser.add_argument("--tile_size", type=int, default=256)
    parser.add_argument("--stride", type=int, default=256)
    parser.add_argument("--level", type=int, default=0)
    parser.add_argument("--batch_size", type=int, default=64)
    parser.add_argument("--num_workers", type=int, default=2)
    parser.add_argument("--keep_ratio", type=float, default=0.3)
    parser.add_argument("--classnames", nargs="*", default=("normal", "tumor"))
    args = parser.parse_args()

    os.makedirs(args.output_root, exist_ok=True)
    features_dir = os.path.join(args.output_root, "features")
    os.makedirs(features_dir, exist_ok=True)

    encoder = ConchOrClipEncoder(conch_repo=args.conch_repo, conch_ckpt=args.conch_ckpt)

    # Discover WSIs
    wsi_paths: List[str] = []
    for cls in os.listdir(args.wsi_root):
        cls_dir = os.path.join(args.wsi_root, cls)
        if not os.path.isdir(cls_dir):
            continue
        for name in os.listdir(cls_dir):
            if name.lower().endswith((".tif", ".tiff", ".svs")):
                wsi_paths.append(os.path.join(cls_dir, name))

    results: Dict[str, str] = {}
    for wsi_path in tqdm(wsi_paths, desc="Extracting WSIs"):
        slide_id = os.path.splitext(os.path.basename(wsi_path))[0]
        out_dir = os.path.join(features_dir, slide_id)
        res = extract_features_for_wsi(
            wsi_path=wsi_path,
            encoder=encoder,
            tile_size=args.tile_size,
            stride=args.stride,
            level=args.level,
            batch_size=args.batch_size,
            keep_ratio=args.keep_ratio,
            classnames=tuple(args.classnames),
            out_dir=out_dir,
        )
        results[slide_id] = res.features_path

    with open(os.path.join(args.output_root, "index.json"), "w") as f:
        json.dump(results, f)


if __name__ == "__main__":
    main()

