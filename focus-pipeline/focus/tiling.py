import os
from dataclasses import dataclass
from typing import Iterator, Tuple, List

import numpy as np
from PIL import Image
import openslide


@dataclass
class Tile:
    x: int
    y: int
    level: int
    image: Image.Image


def compute_downsample_for_level(slide: openslide.OpenSlide, level: int) -> int:
    return int(slide.level_downsamples[level])


def generate_tiles(
    wsi_path: str,
    level: int,
    tile_size: int,
    stride: int,
    tissue_threshold: float = 0.1,
) -> Iterator[Tile]:
    slide = openslide.OpenSlide(wsi_path)
    downsample = compute_downsample_for_level(slide, level)
    level_dim = slide.level_dimensions[level]
    scale = downsample

    width, height = level_dim
    for y in range(0, height - tile_size + 1, stride):
        for x in range(0, width - tile_size + 1, stride):
            loc_x = x * scale
            loc_y = y * scale
            patch = slide.read_region((loc_x, loc_y), level, (tile_size, tile_size)).convert("RGB")
            if is_tissue(patch, threshold=tissue_threshold):
                yield Tile(x=loc_x, y=loc_y, level=level, image=patch)


def is_tissue(patch: Image.Image, threshold: float = 0.1) -> bool:
    arr = np.array(patch)
    gray = arr.mean(axis=2)
    nonwhite = (gray < 240).mean()
    return nonwhite > threshold


def save_tile_images(tiles: Iterator[Tile], out_dir: str) -> List[str]:
    os.makedirs(out_dir, exist_ok=True)
    paths: List[str] = []
    for idx, tile in enumerate(tiles):
        out = os.path.join(out_dir, f"tile_{idx:06d}_x{tile.x}_y{tile.y}.png")
        tile.image.save(out)
        paths.append(out)
    return paths

