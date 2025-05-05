package com.ydo4ki.vird;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Symbol;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
@Getter
public class Interpreter {
	private final Scope program = new Scope(Vird.GLOBAL);
	
	public Interpreter() {}
	public static Val evaluateFinale(Scope scope, Expr val) {
		Val ret = evaluate(scope, val);
		if (ret instanceof Expr) ret = evaluateFinale(scope, (Expr) ret);
		if (ret instanceof WrappedExpr) return evaluate(scope, ((WrappedExpr) ret).getExpr());
		return ret;
	}
	
	public static Val evaluate(Scope scope, Expr val) {
		Objects.requireNonNull(val, "why null");
		if (val instanceof ExprList) return Objects.requireNonNull(
					evaluate_function(scope, (ExprList) val),
					"Cannot evaluate function: " + val
		);
		if (val instanceof Symbol) return Objects.requireNonNull(
					resolve(scope, (Symbol) val),
					"Cannot resolve symbol: " + val
		);
		return val;
	}
	
	private static Val evaluate_function(Scope scope, ExprList f) {
		Expr functionId = f.get(0);
		Val function = evaluate(scope, functionId);
		if (!(function instanceof Func)) {
			throw new IllegalArgumentException("Function not found: " + functionId);
		}
		Func func = ((Func) function);
		
		final Val[] args;
		{
			List<Expr> args0 = f.getElements();
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		return func.invoke(new Scope(scope), args);
	}
	
	public static Val resolve(Scope caller, Symbol name) {
		return caller.resolve(name.getValue());
	}
	
}
