package com.ydo4ki.vird;

import com.ydo4ki.vird.ast.Expr;
import com.ydo4ki.vird.ast.ExprList;

import java.util.List;

/**
 * @since 6/2/2025 11:07 PM
 */
public class VirdUtil {
	public static Expr[] args(ExprList f) {
		final Expr[] args;
		{
			List<Expr> args0 = f.getElements();
			args0.remove(0);
			args = args0.toArray(new Expr[0]);
		}
		return args;
	}
}
