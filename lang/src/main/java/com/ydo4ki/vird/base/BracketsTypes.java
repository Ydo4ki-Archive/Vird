package com.ydo4ki.vird.base;

import java.util.ArrayList;

/**
 * @since 5/31/2025 3:23 PM
 */
public class BracketsTypes extends ArrayList<BracketsType> {
	
	public BracketsType byOpen(char ch) {
		for (BracketsType value : this) {
			if (ch == value.open) return value;
		}
		return null;
	}
	public BracketsType byClose(char ch) {
		for (BracketsType value : this) {
			if (ch == value.close) return value;
		}
		return null;
	}
	public boolean isBracket(char ch) {
		for (BracketsType value : this) {
			if (ch == value.close || ch == value.open) return true;
		}
		return false;
	}
}
