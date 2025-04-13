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
public final class DList implements SyntaxElement {
	
	public static TypeRef TYPE(BracketsType type) {
		return SyntaxElementType.INSTANCE.ref(
				AndConstraint.of(
						new InstanceOfConstraint(DList.class),
						new DListBracketsConstraint(type)
				)
		);
	}
	
	@Setter
	private Location location;
	private final BracketsType bracketsType;
	private final List<SyntaxElement> elements;
	
	public DList(BracketsType bracketsType, List<SyntaxElement> elements) {
		this.location = new Location(null, 0, 0);
		this.bracketsType = bracketsType;
		this.elements = elements;
	}
	
	public List<SyntaxElement> getElements() {
		return new ArrayList<>(elements);
	}
	
	@Override
	public String toString() {
		return "DList" + bracketsType.open + elements.stream().map(Val::toString).collect(Collectors.joining(" ")) + bracketsType.close;
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
