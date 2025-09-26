from typing import Tuple

import torch
import torch.nn as nn
import torch.nn.functional as F


class ABMIL(nn.Module):
    def __init__(self, in_dim: int, hidden_dim: int = 256, num_classes: int = 2):
        super().__init__()
        self.embed = nn.Sequential(
            nn.Linear(in_dim, hidden_dim), nn.ReLU(), nn.Dropout(0.1)
        )
        self.att_a = nn.Linear(hidden_dim, hidden_dim)
        self.att_b = nn.Linear(hidden_dim, 1)
        self.classifier = nn.Linear(hidden_dim, num_classes)

    def forward(self, x: torch.Tensor) -> Tuple[torch.Tensor, torch.Tensor]:
        # x: [N, D]
        h = self.embed(x)
        a = torch.tanh(self.att_a(h))
        a = self.att_b(a).squeeze(-1)  # [N]
        weights = torch.softmax(a, dim=0)
        z = torch.sum(weights.unsqueeze(-1) * h, dim=0)
        logits = self.classifier(z)
        return logits, weights


class TransformerMIL(nn.Module):
    def __init__(self, in_dim: int, embed_dim: int = 256, depth: int = 2, num_heads: int = 8, num_classes: int = 2):
        super().__init__()
        self.proj = nn.Linear(in_dim, embed_dim)
        encoder_layer = nn.TransformerEncoderLayer(d_model=embed_dim, nhead=num_heads, batch_first=True)
        self.encoder = nn.TransformerEncoder(encoder_layer, num_layers=depth)
        self.cls = nn.Parameter(torch.zeros(1, 1, embed_dim))
        nn.init.trunc_normal_(self.cls, std=0.02)
        self.head = nn.Linear(embed_dim, num_classes)

    def forward(self, x: torch.Tensor) -> Tuple[torch.Tensor, torch.Tensor]:
        # x: [N, D]
        z = self.proj(x).unsqueeze(0)  # [1, N, E]
        cls = self.cls.expand(z.size(0), -1, -1)  # [1,1,E]
        z = torch.cat([cls, z], dim=1)
        z = self.encoder(z)
        cls_tok = z[:, 0]
        logits = self.head(cls_tok.squeeze(0))
        attn = torch.ones(x.size(0)) / x.size(0)
        return logits, attn

