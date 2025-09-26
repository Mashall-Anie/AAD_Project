import argparse
import json
import os

import torch

from .tiling import generate_tiles
from .conch_wrapper import ConchOrClipEncoder
from .mil_models import ABMIL


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--wsi_path", required=True)
    parser.add_argument("--checkpoint", required=True)
    parser.add_argument("--conch_repo", default=None)
    parser.add_argument("--conch_ckpt", default=None)
    parser.add_argument("--tile_size", type=int, default=256)
    parser.add_argument("--stride", type=int, default=256)
    parser.add_argument("--level", type=int, default=0)
    parser.add_argument("--batch_size", type=int, default=64)
    parser.add_argument("--out_json", required=True)
    args = parser.parse_args()

    device = torch.device("cpu")
    encoder = ConchOrClipEncoder(conch_repo=args.conch_repo, conch_ckpt=args.conch_ckpt, device="cpu")

    tiles = list(generate_tiles(args.wsi_path, level=args.level, tile_size=args.tile_size, stride=args.stride))
    pil_list = [t.image for t in tiles]
    feats = encoder.encode_pil_batch(pil_list, batch_size=args.batch_size)

    model = ABMIL(in_dim=feats.shape[1], hidden_dim=256, num_classes=2)
    state = torch.load(args.checkpoint, map_location="cpu")
    model.load_state_dict(state)
    model.eval()

    with torch.no_grad():
        logits, attn = model(feats)
        prob = torch.softmax(logits, dim=-1)[1].item()
        pred = int(prob >= 0.5)

    os.makedirs(os.path.dirname(args.out_json) or ".", exist_ok=True)
    with open(args.out_json, "w") as f:
        json.dump({"prob_tumor": prob, "pred": pred}, f, indent=2)


if __name__ == "__main__":
    main()

