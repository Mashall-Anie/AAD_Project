import json
import os
from typing import List, Dict

import pandas as pd
from tabulate import tabulate


def export_table_from_histories(histories: Dict[str, str], out_path: str) -> None:
    """
    histories: map from method name to metrics.json path
    Writes a simple table with best AUC row per method.
    """
    rows: List[Dict] = []
    for name, path in histories.items():
        with open(path, "r") as f:
            hist = json.load(f)
        if not hist:
            continue
        best = max(hist, key=lambda r: (r.get("AUC") or 0.0))
        rows.append({
            "Method": name,
            "AUC": best.get("AUC"),
            "ACC": best.get("ACC"),
            "F1": best.get("F1"),
        })
    df = pd.DataFrame(rows)
    with open(out_path, "w") as f:
        f.write(tabulate(df, headers="keys", tablefmt="github", showindex=False))

