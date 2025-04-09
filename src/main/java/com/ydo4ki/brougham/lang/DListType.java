package com.ydo4ki.brougham.lang;

import java.util.Arrays;

public final class DListType extends Type {
	private static final DListType[] instances = Arrays.stream(BracketsType.values()).map(DListType::new).toArray(DListType[]::new);
	
	private final BracketsType bracketsType;
	
	private DListType(BracketsType bracketsType) {
		this.bracketsType = bracketsType;
	}
	
	public BracketsType getBracketsType() {
		return bracketsType;
	}
	
	public static DListType of(BracketsType bracketsType) {
		return instances[bracketsType.ordinal()];
	}
	
	@Override
	public String toString() {
		return "DList";
	}
}
