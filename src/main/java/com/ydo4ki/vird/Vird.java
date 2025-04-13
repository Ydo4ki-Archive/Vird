package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

/**
 * @author Sulphuris
 * @since 4/13/2025 11:05 PM
 */
public class Vird {
	private Vird() {
	}
	
	private static final FunctionType arithmeticFnType = new FunctionType(
			BlobType.of(4).ref(),
			new TypeRef[]{
					BlobType.of(4).ref(),
					BlobType.of(4).ref(),
					BlobType.of(4).vararg(),
			}
	);
	public static final Func evaluateFinale = Func.intrinsic(null, new TypeRef[]{Expr.TYPE.ref()},
			(caller, args) -> Interpreter.evaluateFinale(caller, null, (Expr) args[0])
	);
	public static final Func evaluate = Func.intrinsic(null, new TypeRef[]{Expr.TYPE.ref()},
			(caller, args) -> Interpreter.evaluate(caller, null, (Expr) args[0])
	);
	public static final Scope GLOBAL = new Scope(null)
			.d("Expr", Expr.TYPE.ref())
			.d("Sym", Symbol.TYPE)
			.d("Type", MetaType.of(0).ref())
			.d("Blob", Func.intrinsic(TypeRef.TYPE.ref(), new TypeRef[]{BlobType.of(4).ref()},
					(caller, args) -> BlobType.of(((Blob) args[0]).toInt()).ref())
			).d("blob4",
					Func.intrinsic(BlobType.of(4).ref(), new TypeRef[]{Symbol.TYPE},
							(caller, args) -> Blob.ofInt(Integer.parseInt(((Symbol) args[0]).getValue())))
			).d("blob1",
					Func.intrinsic(BlobType.of(1).ref(), new TypeRef[]{Symbol.TYPE},
							(caller, args) -> new Blob(new byte[]{(byte) Integer.parseInt(((Symbol) args[0]).getValue())}))
			).d("macro", Func.intrinsic(null, new TypeRef[]{ExprList.TYPE(BracketsType.SQUARE), Expr.TYPE.ref()},
					(caller, args) -> {
						ExprList parameters = ((ExprList) args[0]);
						Expr body = (Expr) args[1];
						return macro(parameters, body);
					})
			).d("eval", evaluate)
			.d("fineval", evaluateFinale)
			.d("fn",
					Func.intrinsic(null, new TypeRef[]{
									ExprList.TYPE(BracketsType.SQUARE),
									new Symbol(new Location(null, 0, 0), ":").getType(),
									TypeRef.TYPE.ref(),
									Expr.TYPE.ref(),
							},
							(caller, args) -> fn(caller, ((ExprList) args[0]), (TypeRef) args[2], (Expr) args[3])
					)
			)
			
			.d("+",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> plus(args))
			).d("*",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> multiply(args))
			).d("-",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> minus(args))
			).d("/",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> divide(args))
			).d("baseType",
					Func.intrinsic(null, new TypeRef[]{TypeRef.TYPE.ref()},
							(caller, args) -> ((TypeRef) args[0]).getBaseType())
			).d("typeOf",
					Func.intrinsic(TypeRef.TYPE.ref(), new TypeRef[]{Expr.TYPE.ref()},
							(caller, args) -> typeOf(caller, (Expr) args[0]))
			).d("charextract",
					Func.intrinsic(null, new TypeRef[]{Symbol.TYPE},
							(caller, args) -> charExtract((Symbol)args[0])
					)
			).d("::",
					Func.intrinsic(null, new TypeRef[]{Symbol.TYPE, Expr.TYPE.ref()},
							(caller, args) ->
									define(caller, ((Symbol) args[0]).getValue(), null, (Expr) args[1]))
			).d(":",
					Func.intrinsic(null, new TypeRef[]{TypeRef.TYPE.ref(), Symbol.TYPE, Expr.TYPE.ref()},
							(caller, args) ->
									define(caller, ((Symbol) args[1]).getValue(), ((TypeRef) args[0]), (Expr) args[2]))
			)
			;
	
	public static Func macro(ExprList parameters, Expr body) {
		TypeRef[] paramTypes = new TypeRef[parameters.elementsCount()];
		String[] paramNames = new String[parameters.elementsCount()];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = Expr.TYPE.ref();
			paramNames[i] = ((Symbol) parameters.getElement(i)).getValue();
		}
		FunctionType functionType = new FunctionType(Expr.TYPE.ref(), paramTypes);
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
		for (int i = 1; i < args.length;
			 ret = operator.applyAsInt(ret, ((Blob) args[i]).toInt()), ++i);
		return Blob.ofInt(ret);
	}
	
	private static Expr replaceDefined(Expr e, Val[] args, String[] names) {
		if (e instanceof ExprList) {
			List<Expr> elements = ((ExprList) e).getElements();
			elements.replaceAll(syntaxElement -> replaceDefined(syntaxElement, args, names));
			return new ExprList(((ExprList) e).getBracketsType(), elements);
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
	
	public static Func fn(Scope scope, ExprList parameters, TypeRef returnType, Expr body) {
		TypeRef[] paramTypes = new TypeRef[parameters.elementsCount()];
		String[] paramNames = new String[parameters.elementsCount()];
		{
			int i = 0;
			for (Iterator<Expr> iterator = parameters.iterator(); iterator.hasNext(); ++i) {
				ExprList p = (ExprList) iterator.next();
				paramTypes[i] = (TypeRef) Interpreter.evaluate(scope, TypeRef.TYPE.ref(), p.getElement(0));
				paramNames[i] = ((Symbol) p.getElement(1)).getValue();
			}
		}
		FunctionType functionType = new FunctionType(returnType, paramTypes);
		return new Func(functionType,
				(c, a) -> {
					for (int i = 0; i < a.length;
						 c.define(paramNames[i], a[i]), ++i);
					return Interpreter.evaluate(c, returnType, body);
				}
		);
	}
	
	public static Blob charExtract(Symbol arg) {
		char[] v = arg.getValue().toCharArray();
		int Len = v.length;
		byte[] d = new byte[Len];
		for (int i = 0; i < Len; d[i] = (byte)v[i], ++i);
		return new Blob(d);
	}
}
