package com.ydo4ki.brougham.lang;

public final class DListType extends Type {
	public static final DListType instance = new DListType();
	
	private DListType() {}
	
	@Override
	public String toString() {
		return "DList";
	}
}
