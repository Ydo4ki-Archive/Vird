package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.lang.constraint.Constraint;
import lombok.Getter;

/**
 * @since 6/5/2025 9:42 PM
 * 
 */
public class IdentityType implements Type {
	@Getter
	private final Constraint implications;
	
	public IdentityType(Constraint implications) {
		this.implications = implications;
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
	
	@Override
	public String toString() {
		return "Type(" + implications + ")";
	}
	
	private static int hash_seq = 1;
	private final int hash = hash_seq++;
	
	@Override
	public boolean equals(Object o) {
		return this == o;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
}
