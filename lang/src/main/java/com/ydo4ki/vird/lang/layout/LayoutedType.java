package com.ydo4ki.vird.lang.layout;

import com.ydo4ki.vird.base.Type;

/**
 * @since 4/19/2025 5:18 PM
 * @author Sulphuris
 */
public class LayoutedType extends Type {
	private final Layout layout;
	
	public LayoutedType(Layout layout) {
		this.layout = layout;
	}
	
	public Layout getLayout() {
		return layout;
	}
}
