package com.ydo4ki.vird.lang.layout;

import com.ydo4ki.vird.base.Type;
import com.ydo4ki.vird.base.Val;
import lombok.Getter;

/**
 * @since 4/19/2025 6:46 PM
 * @author Sulphuris
 */
@Getter
public class Layout implements Val {
	private final long size;
	private final long alignment;
	
	public Layout(long size, long alignment) {
		this.size = size;
		this.alignment = alignment;
	}
	
	@Override
	public Type getRawType() {
		return TYPE;
	}
	
	public static final Type TYPE = new Type() {
		@Override
		public String toString() {
			return "Layout";
		}
	};
}
