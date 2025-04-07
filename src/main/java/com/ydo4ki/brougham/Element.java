package com.ydo4ki.brougham;

/**
 * @since 4/6/2025 8:42 PM
 * @author Sulphuris
 */
public abstract class Element {
	private final Group parent;
	
	protected Element(Group parent) {
		this.parent = parent;
	}
	
	public final String toStringContent() {
		return toStringContent("");
	}
	
	protected abstract String toStringContent(String linePrefix);
}
