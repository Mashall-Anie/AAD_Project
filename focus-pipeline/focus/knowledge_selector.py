from typing import Optional, Tuple

import torch
import torch.nn as nn
import torch.nn.functional as F


class KnowledgeGuidedSelector(nn.Module):
    """
    Simple FOCUS-style selector: compute relevance scores w.r.t. class prompts
    using cosine similarity between patch embeddings and class text embeddings.
    Top-k% patches are kept per WSI.
    """

    def __init__(
        self,
        text_embedder: nn.Module,
        classnames: Tuple[str, ...],
        keep_ratio: float = 0.3,
    ) -> None:
        super().__init__()
        self.text_embedder = text_embedder
        self.keep_ratio = keep_ratio
        with torch.no_grad():
            self.register_buffer("class_text_features", self.text_embedder(classnames))

    @torch.no_grad()
    def forward(self, patch_features: torch.Tensor) -> torch.Tensor:
        # patch_features: [N, D]
        pf = F.normalize(patch_features, dim=-1)
        tf = F.normalize(self.class_text_features, dim=-1)  # [C, D]
        # Relevance per patch as max similarity to any class prompt
        sim = pf @ tf.t()  # [N, C]
        rel = sim.max(dim=1).values  # [N]
        k = max(1, int(rel.numel() * self.keep_ratio))
        topk_idx = torch.topk(rel, k=k, largest=True).indices
        mask = torch.zeros_like(rel, dtype=torch.bool)
        mask[topk_idx] = True
        return mask


class SimpleTextEmbedder(nn.Module):
    """Use open_clip text encoder as a stand-in for language knowledge."""

    def __init__(self, device: Optional[str] = None) -> None:
        super().__init__()
        import open_clip
        self.device = device or ("cuda" if torch.cuda.is_available() else "cpu")
        self.model, _, self.tokenizer = open_clip.create_model_and_transforms(
            "ViT-B-16", pretrained="laion2b_s34b_b88k"
        )
        self.model = self.model.to(self.device)
        self.model.eval()

    @torch.inference_mode()
    def forward(self, classnames: Tuple[str, ...]) -> torch.Tensor:
        import open_clip
        tokens = open_clip.tokenize(list(classnames)).to(self.device)
        text_features = self.model.encode_text(tokens).float()
        return text_features

