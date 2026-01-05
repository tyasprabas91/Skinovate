"""
Helper script untuk validasi dan quality check labeling CSV
"""

import pandas as pd
from pathlib import Path
from typing import Dict, List, Tuple


class LabelingValidator:
    """Class untuk validasi labeling CSV"""
    
    # Valid values untuk setiap column
    VALID_VALUES = {
        'skin_type': ['oily', 'dry', 'combination', 'sensitive', 'normal', ''],
        'acne_type': ['none', 'blackheads', 'whiteheads', 'papules', 'pustules', 'cystic', 'nodular', 'mixed', ''],
        'acne_scars': ['0', '1', ''],
        'acne_scar_severity': ['0', '1', '2', '3', ''],
        'dark_spots': ['0', '1', ''],
        'dark_spots_severity': ['0', '1', '2', '3', ''],
        'wrinkles': ['0', '1', ''],
        'wrinkle_severity': ['0', '1', '2', '3', ''],
        'pores': ['0', '1', ''],
        'pore_size': ['0', '1', '2', ''],
        'texture': ['smooth', 'rough', 'bumpy', 'uneven', ''],
        'texture_score': range(0, 101),
        'hydration': ['0', '1', '2', '3', ''],
        'hydration_score': range(0, 101),
        'redness': ['0', '1', ''],
        'redness_level': ['0', '1', '2', '3', ''],
        'uneven_tone': ['1', '2', '3', '4', '5', ''],
        'tone_score': range(0, 101),
        'sun_damage': ['0', '1', '2', '3', ''],
        'overall_score': range(0, 101),
    }
    
    REQUIRED_COLUMNS = [
        'image_path', 'skin_type', 'acne_type', 'acne_scars', 'dark_spots',
        'wrinkles', 'pores', 'texture', 'hydration', 'redness', 'uneven_tone',
        'sun_damage', 'overall_score'
    ]
    
    def __init__(self, csv_path: str):
        self.csv_path = Path(csv_path)
        self.df = None
        self.errors = []
        self.warnings = []
    
    def load_csv(self) -> bool:
        """Load CSV file"""
        if not self.csv_path.exists():
            self.errors.append(f"CSV file not found: {self.csv_path}")
            return False
        
        try:
            self.df = pd.read_csv(self.csv_path)
            return True
        except Exception as e:
            self.errors.append(f"Error loading CSV: {str(e)}")
            return False
    
    def validate_columns(self) -> bool:
        """Validate that all required columns exist"""
        if self.df is None:
            return False
        
        missing_columns = set(self.REQUIRED_COLUMNS) - set(self.df.columns)
        if missing_columns:
            self.errors.append(f"Missing columns: {missing_columns}")
            return False
        
        return True
    
    def validate_values(self) -> Tuple[int, int]:
        """Validate values in each column"""
        if self.df is None:
            return 0, 0
        
        error_count = 0
        warning_count = 0
        
        for col in self.df.columns:
            if col == 'image_path' or col == 'notes':
                continue
            
            if col not in self.VALID_VALUES:
                continue
            
            valid_set = set(str(v) for v in self.VALID_VALUES[col])
            
            # Check for invalid values
            invalid_mask = ~self.df[col].astype(str).isin(valid_set)
            invalid_count = invalid_mask.sum()
            
            if invalid_count > 0:
                invalid_rows = self.df[invalid_mask][['image_path', col]]
                self.errors.append(f"\nInvalid values in '{col}' ({invalid_count} rows):")
                for _, row in invalid_rows.head(10).iterrows():
                    self.errors.append(f"  {row['image_path']}: {row[col]}")
                error_count += invalid_count
        
        return error_count, warning_count
    
    def validate_logic(self) -> int:
        """Validate logical consistency (e.g., if acne_scars=0, severity should be 0)"""
        if self.df is None:
            return 0
        
        logic_errors = 0
        
        # Check: if acne_scars = 0, acne_scar_severity should be 0
        mask = (self.df['acne_scars'].astype(str) == '0') & (self.df['acne_scar_severity'].astype(str) != '0')
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have acne_scars=0 but severity != 0")
            logic_errors += mask.sum()
        
        # Check: if acne_scars = 1, acne_scar_severity should be 1-3
        mask = (self.df['acne_scars'].astype(str) == '1') & (~self.df['acne_scar_severity'].astype(str).isin(['1', '2', '3']))
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have acne_scars=1 but severity not 1-3")
            logic_errors += mask.sum()
        
        # Similar checks for other features
        # dark_spots & dark_spots_severity
        mask = (self.df['dark_spots'].astype(str) == '0') & (self.df['dark_spots_severity'].astype(str) != '0')
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have dark_spots=0 but severity != 0")
            logic_errors += mask.sum()
        
        # wrinkles & wrinkle_severity
        mask = (self.df['wrinkles'].astype(str) == '0') & (self.df['wrinkle_severity'].astype(str) != '0')
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have wrinkles=0 but severity != 0")
            logic_errors += mask.sum()
        
        # pores & pore_size
        mask = (self.df['pores'].astype(str) == '0') & (self.df['pore_size'].astype(str) != '0')
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have pores=0 but size != 0")
            logic_errors += mask.sum()
        
        # redness & redness_level
        mask = (self.df['redness'].astype(str) == '0') & (self.df['redness_level'].astype(str) != '0')
        if mask.sum() > 0:
            self.warnings.append(f"\nLogic error: {mask.sum()} rows have redness=0 but level != 0")
            logic_errors += mask.sum()
        
        return logic_errors
    
    def get_statistics(self) -> Dict:
        """Get statistics about the dataset"""
        if self.df is None:
            return {}
        
        stats = {
            'total_images': len(self.df),
            'completed_labels': len(self.df[self.df['skin_type'] != '']),
            'completion_rate': len(self.df[self.df['skin_type'] != '']) / len(self.df) * 100,
        }
        
        # Distribution statistics
        if 'skin_type' in self.df.columns:
            stats['skin_type_distribution'] = self.df['skin_type'].value_counts().to_dict()
        
        if 'acne_type' in self.df.columns:
            stats['acne_type_distribution'] = self.df['acne_type'].value_counts().to_dict()
        
        # Score statistics
        numeric_columns = ['texture_score', 'hydration_score', 'tone_score', 'overall_score']
        for col in numeric_columns:
            if col in self.df.columns:
                non_empty = pd.to_numeric(self.df[col], errors='coerce').dropna()
                if len(non_empty) > 0:
                    stats[f'{col}_mean'] = non_empty.mean()
                    stats[f'{col}_std'] = non_empty.std()
                    stats[f'{col}_min'] = non_empty.min()
                    stats[f'{col}_max'] = non_empty.max()
        
        return stats
    
    def validate_all(self) -> bool:
        """Run all validations"""
        if not self.load_csv():
            return False
        
        if not self.validate_columns():
            return False
        
        error_count, warning_count = self.validate_values()
        logic_errors = self.validate_logic()
        
        return error_count == 0 and logic_errors == 0
    
    def print_report(self):
        """Print validation report"""
        print("=" * 60)
        print("LABELING VALIDATION REPORT")
        print("=" * 60)
        
        if self.df is None:
            print("\n❌ Could not load CSV file")
            for error in self.errors:
                print(f"  {error}")
            return
        
        print(f"\n📊 Dataset Info:")
        print(f"   Total rows: {len(self.df)}")
        print(f"   Total columns: {len(self.df.columns)}")
        
        # Statistics
        stats = self.get_statistics()
        print(f"\n📈 Statistics:")
        print(f"   Completed labels: {stats.get('completed_labels', 0)}")
        print(f"   Completion rate: {stats.get('completion_rate', 0):.1f}%")
        
        if 'skin_type_distribution' in stats:
            print(f"\n   Skin Type Distribution:")
            for skin_type, count in stats['skin_type_distribution'].items():
                if skin_type:
                    print(f"     {skin_type}: {count}")
        
        # Errors
        if self.errors:
            print(f"\n❌ Errors ({len(self.errors)}):")
            for error in self.errors[:20]:  # Limit to first 20
                print(f"   {error}")
            if len(self.errors) > 20:
                print(f"   ... and {len(self.errors) - 20} more errors")
        else:
            print(f"\n✅ No errors found!")
        
        # Warnings
        if self.warnings:
            print(f"\n⚠️  Warnings ({len(self.warnings)}):")
            for warning in self.warnings:
                print(f"   {warning}")
        else:
            print(f"\n✅ No warnings!")
        
        print("\n" + "=" * 60)


def main():
    """Main function"""
    import sys
    
    if len(sys.argv) < 2:
        print("Usage: python labeling_helper.py <csv_file>")
        print("Example: python labeling_helper.py datasets/annotations/labeling_template.csv")
        return
    
    csv_path = sys.argv[1]
    validator = LabelingValidator(csv_path)
    
    is_valid = validator.validate_all()
    validator.print_report()
    
    if is_valid:
        print("\n✅ Validation passed!")
        sys.exit(0)
    else:
        print("\n❌ Validation failed. Please fix errors above.")
        sys.exit(1)


if __name__ == "__main__":
    main()

