package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public final class DListType extends Type {
	private static final DListType[] instances = Arrays.stream(BracketsType.values()).map(DListType::new).toArray(DListType[]::new);
	
	private final BracketsType bracketsType;
	
	public static DListType of(BracketsType bracketsType) {
		return instances[bracketsType.ordinal()];
	}
	
	@Override
	public String toString() {
		return "DList";
	}
}
