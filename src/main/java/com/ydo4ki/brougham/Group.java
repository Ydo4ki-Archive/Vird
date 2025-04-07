package com.ydo4ki.brougham;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 4/6/2025 8:41 PM
 * @author Sulphuris
 */
public class Group extends Element {
	private final BracketsType type;
	private final List<Element> elements;
	
	public Group(BracketsType type, List<Element> elements) {
		this.type = type;
		this.elements = elements;
	}
	
	public List<Element> getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("group ").append(type.open);
		for (Element element : elements) {
			b.append(element).append(" ");
		}
		b.append(type.close);
		return b.toString();
	}
}
