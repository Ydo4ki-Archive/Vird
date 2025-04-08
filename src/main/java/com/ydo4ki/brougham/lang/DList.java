package com.ydo4ki.brougham.lang;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
public final class DList implements Val {
	private final BracketsType bracketsType;
	private final List<Val> elements;
	
	public DList(DList parent, BracketsType bracketsType, List<Val> elements) {
		this.bracketsType = bracketsType;
		this.elements = elements;
	}
	
	public List<Val> getElements() {
		return elements;
	}
	
	public BracketsType getBracketsType() {
		return bracketsType;
	}
	
	@Override
	public Type getType() {
		return DListType.instance;
	}
	
	@Override
	public String toString() {
		return "DList"+bracketsType.open + elements.stream().map(Val::toString).collect(Collectors.joining(" "))+bracketsType.close;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		DList dList = (DList) o;
		return bracketsType == dList.bracketsType && Objects.equals(elements, dList.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(bracketsType, elements);
	}
}
