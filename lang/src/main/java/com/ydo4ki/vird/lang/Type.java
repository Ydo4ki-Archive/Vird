package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import lombok.Getter;

import java.util.ArrayList;

/**
 * @since 6/5/2025 9:42 PM
 * @author alignie
 */
public class Type implements Val {
	private final int depth;
	@Getter
	private final Constraint implications;
	
	public Type(Constraint implications) {
		this.depth = -1;
		this.implications = implications;
	}
	
	private Type(int depth, Constraint implications) {
		this.depth = depth;
		this.implications = implications;
	}
	
	@Override
	public Type getType() {
		return getMetaType(this.depth+1);
	}
	
	@Override
	public String toString() {
		return "Type(" + implications + ")";
	}
	
	private final int hash = hash_seq++;
	
	@Override
	public boolean equals(Object o) {
		return this == o;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	private static int hash_seq = 0;
	private static final ArrayList<Type> meta = new ArrayList<>();
	
	
	private static Type getMetaType(int depth) {
		if (depth < 0) throw new IllegalArgumentException(depth + " < 0");
		while (meta.size() <= depth) {
			Constraint c = depth == 0
					? FreeConstraint.INSTANCE
					: new EqualityConstraint(getMetaType(depth-1)); // singleton ???
			meta.add(new Type(meta.size(), c));
		}
		return meta.get(depth);
	}
	
	// todo: separate for actual signatures
	public static final Type ROOT_FUNCTION = new Type(FreeConstraint.INSTANCE);
}
