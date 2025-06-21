package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Type;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
@Getter
public final class ExprList extends Expr implements Iterable<Expr> {
	
	private final BracketsType bracketsType;
	private final List<Expr> elements;
	
	ExprList(Location location, BracketsType bracketsType, List<Expr> elements) {
		super(location);
		this.bracketsType = bracketsType;
		this.elements = elements;
	}
	
	public static ExprList of(Location location, BracketsType bracketsType, List<Expr> elements) {
		if (bracketsType == null) throw new NullPointerException("bracketsType is null");
		return new ExprList(location, bracketsType, elements);
	}
	
	@Override
	public final Collection<? extends Expr> split(String... separateLines) {
		return Collections.singleton(splitList(separateLines));
	}
	
	public ExprList splitList(String... separateLines) {
		return new ExprList(getLocation(), bracketsType,
				getElements().stream()
						.flatMap(e -> e.split(separateLines).stream())
						.collect(Collectors.toList()));
	}
	
	public List<Expr> getElements() {
		return new ArrayList<>(elements);
	}
	
	public int size() {
		return elements.size();
	}
	
	public Expr get(int index) {
		return elements.get(index);
	}
	
	@Override
	public String toString() {
		return getBracketsType().open + elements.stream().map(Val::toString).collect(Collectors.joining(" ")) + getBracketsType().close;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ExprList exprList = (ExprList) o;
		return Objects.equals(elements, exprList.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getBracketsType(), elements);
	}
	
	@Override
	public Iterator<Expr> iterator() {
		return elements.iterator();
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
	
	public static final Type TYPE = new Type(FreeConstraint.INSTANCE);
}
