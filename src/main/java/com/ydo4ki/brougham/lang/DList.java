package com.ydo4ki.brougham.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
public final class DList implements Val {
	private final DList parent;
	private final BracketsType bracketsType;
	private final List<Val> elements;
	
	private final Map<String, Val> definedSymbols = new HashMap<>();
	
	public DList(DList parent, BracketsType bracketsType, List<Val> elements) {
		this.parent = parent;
		this.bracketsType = bracketsType;
		this.elements = elements;
	}
	
	public FunctionSet resolveFunction(Symbol symbol) {
		Val dereferenced = definedSymbols.get(symbol.getValue());
		if (dereferenced instanceof FunctionSet) return (FunctionSet)dereferenced;
		return parent == null ? null : parent.resolveFunction(symbol);
	}
	
	public void define(Symbol id, Val values) {
		String name = id.getValue();
		if (definedSymbols.containsKey(name))
			throw new IllegalArgumentException(id + " is already defined");
		definedSymbols.put(name, values);
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
