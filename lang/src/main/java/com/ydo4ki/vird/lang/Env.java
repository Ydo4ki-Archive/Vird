package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.Expr;

/**
 * @since 7/5/2025 6:04 PM
 
 */
public interface Env {
	ValidatedValCall preresolve(String name, Expr expr) throws LangValidationException;
	
	Val resolve(String name) throws RuntimeOperation;
}
