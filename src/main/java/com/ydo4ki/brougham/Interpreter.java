package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;
import com.ydo4ki.brougham.lib.Std;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
@Getter
public class Interpreter {
	private final Scope program = new Scope(null);
	
	public Interpreter() {
		Std.setup(program);
	}
	
	public Val next(String in) throws IOException {
		return evaluate(program, null, new Parser().read(new Source.OfString(in)));
	}
	
	public Val next(BufferedReader in) throws IOException {
		return evaluate(program, null, new Parser().read(new Source.Raw(in)));
	}
	
	public Val next(Source in) throws IOException {
		SyntaxElement parsed = new Parser().read(in);
		if (parsed == null) throw new EOFException();
		return evaluateFinale(program, null, parsed);
	}
	
	public static Val evaluateFinale(Scope scope, TypeRef expectedType, SyntaxElement val) {
		Val ret = evaluate(scope, expectedType, val);
		if (ret instanceof SyntaxElement) ret = evaluateFinale(scope, expectedType, (SyntaxElement) ret);
		return ret;
	}
	
	public static Val evaluate(Scope scope, TypeRef expectedType, SyntaxElement val) {
		Objects.requireNonNull(val, "why null");
		if (expectedType != null && expectedType.matches(scope, val)) return val;
		if (val instanceof DList) return Objects.requireNonNull(
					evaluate_function(scope, expectedType, (DList) val),
					"Cannot evaluate function: " + val
		);
		if (val instanceof Symbol) return Objects.requireNonNull(
					resolve(scope, (Symbol) val),
					"Cannot resolve symbol: " + val
		);
		return val;
	}
	
	private static Val evaluate_function(Scope scope, TypeRef expectedType, DList f) {
		SyntaxElement functionId = f.getElements().get(0);
		Val function = evaluate(scope, null, functionId);
		if (!(function instanceof Func)) {
			f.getLocation().print(System.err);
			throw new IllegalArgumentException("Function not found: " + functionId);
		}
		Func func = ((Func) function);
		
		final Val[] args;
		{
			List<SyntaxElement> args0 = f.getElements();
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		return func.invoke(new Scope(scope), args);
	}
	
	public static Val resolve(Scope caller, Symbol name) {
		return caller.resolve(name.getValue());
	}
	
}
