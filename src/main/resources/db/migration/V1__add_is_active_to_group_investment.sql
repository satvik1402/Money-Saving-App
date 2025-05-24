-- Add is_active column to group_investment_pots table
ALTER TABLE group_investment_pots ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true; 