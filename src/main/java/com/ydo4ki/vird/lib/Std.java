package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.Interpreter;
import com.ydo4ki.vird.Location;
import com.ydo4ki.vird.lang.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

/**
 * @author Sulphuris
 * @since 4/12/2025 4:20 PM
 */
public final class Std {
	private Std() {
	}
	
	public static final Func evaluate = Func.intrinsic(null, new TypeRef[]{Expr.TYPE.ref()},
			(caller, args) -> Interpreter.evaluate(caller, null, (Expr) args[0])
	);
	public static final Func evaluateFinale = Func.intrinsic(null, new TypeRef[]{Expr.TYPE.ref()},
			(caller, args) -> Interpreter.evaluateFinale(caller, null, (Expr) args[0])
	);
	
	public static Func macro(DList parameters, Expr body) {
		TypeRef[] paramTypes = new TypeRef[parameters.getElements().size()];
		String[] paramNames = new String[parameters.getElements().size()];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = Expr.TYPE.ref();
			paramNames[i] = ((Symbol) parameters.getElements().get(i)).getValue();
		}
		FunctionType functionType = new FunctionType(
				Expr.TYPE.ref(),
				paramTypes
		);
		return new Func(
				functionType,
				(c, a) -> replaceDefined(body, a, paramNames)
		);
	}
	
	public static Blob plus(Val[] args) {
		return Blob.ofInt(ints(args).sum());
	}
	public static Blob multiply(Val[] args) {
		return Blob.ofInt(ints(args).reduce(1, (a, b) -> a * b));
	}
	private static IntStream ints(Val[] blobs) {
		return Arrays.stream(blobs).mapToInt(arg -> ((Blob) arg).toInt());
	}
	
	public static Blob minus(Val[] args) {
		return arithReduce(args, (ret, b) -> ret - b);
	}
	
	public static Blob divide(Val[] args) {
		return arithReduce(args, (ret, b) -> ret / b);
	}
	
	private static Blob arithReduce(Val[] args, IntBinaryOperator operator) {
		int ret = ((Blob) args[0]).toInt();
		for (int i = 1; i < args.length; i++) {
			Val arg = args[i];
			ret = operator.applyAsInt(ret, ((Blob) arg).toInt());
		}
		return Blob.ofInt(ret);
	}
	
	private static Expr replaceDefined(Expr e, Val[] args, String[] names) {
		if (e instanceof DList) {
			List<Expr> elements = ((DList) e).getElements();
			elements.replaceAll(syntaxElement -> replaceDefined(syntaxElement, args, names));
			return new DList(((DList) e).getBracketsType(), elements);
		} else if (e instanceof Symbol) {
			for (int i = 0; i < names.length; i++) {
				if (((Symbol) e).getValue().equals(names[i])) return (Expr) args[i];
			}
			return e;
		}
		return e;
	}
	
	public static Val define(Scope scope, String name, TypeRef expectedType, Expr expr) {
		Val value = Interpreter.evaluate(scope, expectedType, expr);
		scope.getParent().define(name, value);
		return value;
	}
	
	public static TypeRef typeOf(Scope scope, Expr expr) {
		Val evaluated;
		try {
			evaluated = Interpreter.evaluate(scope, null, expr);
		} catch (NullPointerException e) {
			evaluated = expr;
		}
		return evaluated.getType();
	}
	public static Func fn(Scope scope, DList parameters, TypeRef returnType, Expr body) {
		TypeRef[] paramTypes = new TypeRef[parameters.getElements().size()];
		String[] paramNames = new String[parameters.getElements().size()];
		int i = 0;
		for (Val element : parameters.getElements()) {
			DList p = (DList) element;
			paramTypes[i] = (TypeRef) Interpreter.evaluate(scope, TypeRef.TYPE.ref(), p.getElements().get(0));
			paramNames[i] = ((Symbol) p.getElements().get(1)).getValue();
			i++;
		}
		FunctionType functionType = new FunctionType(
				returnType,
				paramTypes
		);
		return new Func(functionType,
				(c, a) -> {
					for (int i1 = 0; i1 < a.length; i1++) {
						c.define(paramNames[i1], a[i1]);
					}
					return Interpreter.evaluate(c, returnType, body);
				}
		);
	}
	
	public static Blob charExtract(Symbol arg) {
		char[] v = arg.getValue().toCharArray();
		int Len = v.length;
		byte[] d = new byte[Len];
		for (int i = 0; i < Len; i++) {
			d[i] = (byte)v[i];
		}
		return new Blob(d);
	}
}
