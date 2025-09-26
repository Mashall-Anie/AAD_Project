#!/usr/bin/env python3
"""
Fix FOCUS module dependencies and setup proper Python path
"""

# ==========================================
# CELL 12.1: Fix FOCUS Module Dependencies
# ==========================================

import sys
import os
from pathlib import Path

def fix_focus_modules():
    """Fix FOCUS module import issues"""
    
    focus_dir = Path('/kaggle/working/FOCUS')
    
    print("🔧 Fixing FOCUS module dependencies...")
    
    # Add FOCUS directory to Python path
    if str(focus_dir) not in sys.path:
        sys.path.insert(0, str(focus_dir))
        print(f"✅ Added {focus_dir} to Python path")
    
    # Find all Python package directories in FOCUS
    package_dirs = []
    for item in focus_dir.rglob('__init__.py'):
        package_dir = item.parent
        if str(package_dir) not in sys.path:
            sys.path.insert(0, str(package_dir))
            package_dirs.append(package_dir)
    
    print(f"📦 Added {len(package_dirs)} package directories:")
    for pkg_dir in package_dirs:
        rel_path = pkg_dir.relative_to(focus_dir)
        print(f"  - {rel_path}")
    
    # Check what modules are actually available
    print("\n🔍 Checking available FOCUS modules:")
    
    focus_modules = []
    for py_file in focus_dir.rglob('*.py'):
        if py_file.name != '__init__.py':
            rel_path = py_file.relative_to(focus_dir)
            module_path = str(rel_path).replace('/', '.').replace('.py', '')
            focus_modules.append(module_path)
    
    # Test imports
    working_modules = []
    broken_modules = []
    
    for module in focus_modules[:10]:  # Test first 10 modules
        try:
            __import__(module)
            working_modules.append(module)
            print(f"  ✅ {module}")
        except ImportError as e:
            broken_modules.append((module, str(e)))
            print(f"  ❌ {module}: {e}")
        except Exception as e:
            print(f"  ⚠️ {module}: {e}")
    
    return working_modules, broken_modules

# ==========================================
# CELL 12.2: Inspect FOCUS Structure and Fix Imports
# ==========================================

def inspect_and_fix_focus():
    """Inspect FOCUS structure and fix import issues"""
    
    focus_dir = Path('/kaggle/working/FOCUS')
    
    print("📋 FOCUS Directory Structure:")
    
    # Map directory structure
    for root, dirs, files in os.walk(focus_dir):
        level = root.replace(str(focus_dir), '').count(os.sep)
        indent = '  ' * level
        rel_root = Path(root).relative_to(focus_dir)
        print(f"{indent}📁 {rel_root}/")
        
        # Show Python files
        sub_indent = '  ' * (level + 1)
        for file in files:
            if file.endswith('.py'):
                print(f"{sub_indent}🐍 {file}")
    
    # Check specific missing modules
    missing_modules = [
        'datasets.dataset_generic',
        'utils.core_utils',
        'models',
        'datasets'
    ]
    
    print(f"\n🔍 Checking for missing modules:")
    for module in missing_modules:
        module_path = focus_dir / module.replace('.', '/')
        py_file = module_path.with_suffix('.py')
        init_file = module_path / '__init__.py'
        
        if py_file.exists():
            print(f"  ✅ {module} -> {py_file.relative_to(focus_dir)}")
        elif init_file.exists():
            print(f"  ✅ {module} -> {module_path.relative_to(focus_dir)}/__init__.py")
        else:
            print(f"  ❌ {module} -> NOT FOUND")
            
            # Try to find similar files
            pattern = module.split('.')[-1]
            similar_files = list(focus_dir.rglob(f"*{pattern}*.py"))
            if similar_files:
                print(f"     Similar files found:")
                for sim_file in similar_files[:3]:
                    print(f"       - {sim_file.relative_to(focus_dir)}")

# ==========================================
# CELL 12.3: Create Missing Module Fixes
# ==========================================

def create_missing_modules():
    """Create missing modules or fix imports"""
    
    focus_dir = Path('/kaggle/working/FOCUS')
    
    # Check if datasets directory exists
    datasets_dir = focus_dir / 'datasets'
    if not datasets_dir.exists():
        print("📁 Creating datasets directory...")
        datasets_dir.mkdir(exist_ok=True)
        
        # Create __init__.py
        with open(datasets_dir / '__init__.py', 'w') as f:
            f.write('# FOCUS datasets module\n')
        print("✅ Created datasets/__init__.py")
    
    # Check if dataset_generic.py exists
    dataset_generic = datasets_dir / 'dataset_generic.py'
    if not dataset_generic.exists():
        print("🔧 Creating placeholder dataset_generic.py...")
        
        placeholder_code = '''
# Placeholder for FOCUS dataset_generic module
import pandas as pd
import numpy as np
from pathlib import Path

def save_splits(splits, save_dir, filename='splits.csv'):
    """Save dataset splits"""
    split_df = pd.DataFrame(splits)
    save_path = Path(save_dir) / filename
    save_path.parent.mkdir(parents=True, exist_ok=True)
    split_df.to_csv(save_path, index=False)
    print(f"Splits saved to {save_path}")
    return save_path

def load_splits(split_path):
    """Load dataset splits"""
    return pd.read_csv(split_path)

class GenericDataset:
    """Generic dataset class for FOCUS"""
    def __init__(self, data_dir, split='train'):
        self.data_dir = Path(data_dir)
        self.split = split
        
    def __len__(self):
        return 0
    
    def __getitem__(self, idx):
        return None
'''
        
        with open(dataset_generic, 'w') as f:
            f.write(placeholder_code)
        print("✅ Created datasets/dataset_generic.py")
    
    # Check utils directory
    utils_dir = focus_dir / 'utils'
    if utils_dir.exists():
        print("✅ utils directory exists")
        
        # Check core_utils.py
        core_utils = utils_dir / 'core_utils.py'
        if core_utils.exists():
            print("✅ utils/core_utils.py exists")
            
            # Check if it has the train function
            with open(core_utils, 'r') as f:
                content = f.read()
            
            if 'def train' in content:
                print("✅ train function found in core_utils.py")
            else:
                print("❌ train function not found in core_utils.py")
        else:
            print("❌ utils/core_utils.py not found")

# ==========================================
# CELL 12.4: Alternative Training Approach
# ==========================================

def try_alternative_training():
    """Try alternative ways to run FOCUS training"""
    
    focus_dir = Path('/kaggle/working/FOCUS')
    
    print("🔄 Trying alternative training approaches...")
    
    # Option 1: Check if there are other training scripts
    training_scripts = []
    for py_file in focus_dir.rglob('*.py'):
        with open(py_file, 'r') as f:
            content = f.read()
        
        if any(keyword in content.lower() for keyword in ['argparse', 'def main', 'if __name__']):
            training_scripts.append(py_file)
    
    print(f"📋 Found {len(training_scripts)} potential executable scripts:")
    for script in training_scripts:
        rel_path = script.relative_to(focus_dir)
        print(f"  - {rel_path}")
    
    # Option 2: Try to run main.py with different arguments
    main_py = focus_dir / 'main.py'
    if main_py.exists():
        print(f"\n📖 Analyzing main.py...")
        
        with open(main_py, 'r') as f:
            main_content = f.read()
        
        print("First 20 lines of main.py:")
        lines = main_content.split('\n')
        for i, line in enumerate(lines[:20]):
            print(f"{i+1:2d}: {line}")
        
        # Look for argument parsing
        if 'argparse' in main_content:
            print("\n🔍 Found argparse usage")
            
            # Extract argument patterns
            import re
            arg_patterns = re.findall(r'add_argument\([\'\"](.*?)[\'\"](.*?)\)', main_content)
            
            if arg_patterns:
                print("📋 Available arguments:")
                for pattern in arg_patterns[:10]:
                    print(f"  - {pattern[0]}")
    
    # Option 3: Check for setup.py or requirements
    setup_files = list(focus_dir.glob('setup.py')) + list(focus_dir.glob('requirements*.txt'))
    if setup_files:
        print(f"\n📦 Found setup files:")
        for setup_file in setup_files:
            print(f"  - {setup_file.name}")

# ==========================================
# CELL 12.5: Run All Fixes
# ==========================================

print("🔧 FIXING FOCUS MODULE ISSUES")
print("="*40)

# Step 1: Fix modules
working_modules, broken_modules = fix_focus_modules()

# Step 2: Inspect structure
print("\n" + "="*40)
inspect_and_fix_focus()

# Step 3: Create missing modules
print("\n" + "="*40)
create_missing_modules()

# Step 4: Try alternatives
print("\n" + "="*40)
try_alternative_training()

print("\n" + "="*50)
print("✅ MODULE FIXES COMPLETED")
print("="*50)

print("\n🎯 Next steps:")
print("1. Try running main.py again with fixed modules")
print("2. If still fails, check the alternative training scripts")
print("3. Manual configuration may be needed")