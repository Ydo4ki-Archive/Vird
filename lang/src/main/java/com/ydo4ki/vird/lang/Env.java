package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.Expr;

/**
 * @since 7/5/2025 6:04 PM
 
 */
public interface Env extends Val {
	ValidatedValCall preresolve(Env callerEnv, String name, Expr expr) throws LangValidationException;
	
	Val resolve(Env callerEnv, String name) throws RuntimeOperation;
}
