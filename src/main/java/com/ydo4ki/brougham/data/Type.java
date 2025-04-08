package com.ydo4ki.brougham.data;


/**
* @since 4/7/2025 9:43 PM
* @author Sulphuris
*/
public abstract class Type implements Val {
	@Override
	public boolean isType() {
		return true;
	}
	
	@Override
	public Type getType() {
		return MetaType.of(0);
	}
}

