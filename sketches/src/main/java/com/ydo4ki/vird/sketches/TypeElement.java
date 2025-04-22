package com.ydo4ki.vird.sketches;

/**
 * @since 4/19/2025 3:27 PM
 * @author Sulphuris
 */
abstract class TypeElement {

}

abstract class NamedTypeElement extends TypeElement {
	private final String name;
	
	NamedTypeElement(String name) {
		this.name = name;
	}
}