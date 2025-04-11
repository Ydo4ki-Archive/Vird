package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
@EqualsAndHashCode(callSuper = false)
public final class DList extends Scope implements Val {
	@Getter
	@Setter
	private Location location;
	@Getter
	private final BracketsType bracketsType;
	@Getter
	private final List<Val> elements;
	
	public DList(Scope parent, BracketsType bracketsType, List<Val> elements) {
		super(parent);
		this.location = new Location(null, 0, 0);
		this.bracketsType = bracketsType;
		this.elements = elements;
	}
	

//	public FunctionImpl resolveFunctionExact(Symbol symbol, FunctionType type) {
//		Val dereferenced = definedSymbols.get(symbol.getValue());
//		if (dereferenced instanceof FunctionSet) {
//			FunctionSet set = (FunctionSet) dereferenced;
//			FunctionImpl exact = set.findImplByType(type);
//			if (exact != null) return exact;
//		}
//		return parent == null ? null : parent.resolveFunctionExact(symbol, type);
//	}
	
	
	@Override
	public Type getRawType() {
		return DListType.of(bracketsType);
	}
	
	@Override
	public String toString() {
		return "DList"+bracketsType.open + elements.stream().map(Val::toString).collect(Collectors.joining(" "))+bracketsType.close;
	}
}
