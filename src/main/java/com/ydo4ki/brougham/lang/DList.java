package com.ydo4ki.brougham.lang;

import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.lang.constraint.AndConstraint;
import com.ydo4ki.brougham.lang.constraint.DListBracketsConstraint;
import com.ydo4ki.brougham.lang.constraint.InstanceOfConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public final class DList extends Scope implements SyntaxElement {
	
	public static TypeRef TYPE(BracketsType type) {
		return SyntaxElementType.instance.ref(
				AndConstraint.of(
						new InstanceOfConstraint(DList.class),
						new DListBracketsConstraint(type)
				)
		);
	}
	
	@Setter
	private Location location;
	private final BracketsType bracketsType;
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
	public String toString() {
		return "DList" + bracketsType.open + elements.stream().map(Val::toString).collect(Collectors.joining(" ")) + bracketsType.close;
	}
}
