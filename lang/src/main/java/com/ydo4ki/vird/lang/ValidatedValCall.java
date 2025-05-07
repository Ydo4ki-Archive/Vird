package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.constraint.Constraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 5/7/2025 8:01 PM
 * @author Sulphuris
 */

@RequiredArgsConstructor
public abstract class ValidatedValCall {
	@Getter
	protected final Constraint constraint;
	protected final Scope scope;
	protected final Expr[] args;
	
	public abstract Val invoke();
}
