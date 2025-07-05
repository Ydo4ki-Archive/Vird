package com.ydo4ki.vird.lang;

/**
 * @since 7/5/2025 6:04 PM
 
 */
public interface Env {
	ValidatedValCall preresolve(String name);
	
	Val resolve(String name);
}
