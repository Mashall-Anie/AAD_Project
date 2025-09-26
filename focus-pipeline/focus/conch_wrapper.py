import os
from typing import Optional, Tuple

import torch
import torch.nn as nn


class ConchOrClipEncoder(nn.Module):
    def __init__(
        self,
        conch_repo: Optional[str] = None,
        conch_ckpt: Optional[str] = None,
        device: Optional[str] = None,
    ) -> None:
        super().__init__()
        self.device = torch.device(device or ("cuda" if torch.cuda.is_available() else "cpu"))
        self.encoder, self.preprocess, self.embed_dim = self._build(conch_repo, conch_ckpt)
        self.encoder.eval()

    def _build(self, conch_repo: Optional[str], conch_ckpt: Optional[str]):
        # Try CONCH
        if conch_repo and os.path.isdir(conch_repo) and conch_ckpt and os.path.isfile(conch_ckpt):
            try:
                import sys
                sys.path.insert(0, conch_repo)
                from conch.open_clip_custom.factory import create_model_and_transforms
                model, _, preprocess = create_model_and_transforms(
                    model_name="conch_ViT-B-16", pretrained=conch_ckpt
                )
                model.to(self.device)
                embed_dim = model.visual.output_dim
                return model, preprocess, embed_dim
            except Exception:
                pass

        # Fallback to open-clip ViT-B-16
        import open_clip
        model, _, preprocess = open_clip.create_model_and_transforms(
            "ViT-B-16", pretrained="laion2b_s34b_b88k"
        )
        model.to(self.device)
        embed_dim = model.visual.output_dim
        return model, preprocess, embed_dim

    @torch.inference_mode()
    def forward(self, images: torch.Tensor) -> torch.Tensor:
        # images are already preprocessed tensors [B,3,H,W]
        features = self.encoder.encode_image(images.to(self.device))
        if hasattr(features, "float"):
            features = features.float()
        return features

    def encode_pil_batch(self, pil_list, batch_size: int = 64) -> torch.Tensor:
        import torchvision.transforms as T
        from torch.utils.data import DataLoader, Dataset

        preprocess = self.preprocess

        class PILSet(Dataset):
            def __init__(self, items):
                self.items = items

            def __len__(self):
                return len(self.items)

            def __getitem__(self, idx):
                return preprocess(self.items[idx])

        loader = DataLoader(PILSet(pil_list), batch_size=batch_size, shuffle=False, num_workers=2)
        outs = []
        for batch in loader:
            outs.append(self.forward(batch))
        return torch.cat(outs, dim=0)

