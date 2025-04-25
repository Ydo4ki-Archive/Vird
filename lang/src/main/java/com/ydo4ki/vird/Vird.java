package com.ydo4ki.vird;

import com.ydo4ki.vird.base.BracketsType;
import com.ydo4ki.vird.base.MetaType;
import com.ydo4ki.vird.base.TypeRef;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Symbol;
import com.ydo4ki.vird.lang.layout.ByLayoutType;
import com.ydo4ki.vird.lang.layout.EnumLayout;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private static final TypeRef BLOB4_REF = BlobType.of(4).ref();
    private static final TypeRef EXPR_REF = Expr.TYPE_RAW.ref();
    private static final TypeRef TYPE_REF = TypeRef.TYPE.ref();

    public static final Func evaluateFinale = Func.intrinsic(null, new TypeRef[]{EXPR_REF},
            (caller, args) -> Interpreter.evaluateFinale(caller, null, (Expr) args[0])
    );

    public static final Func evaluate = Func.intrinsic(null, new TypeRef[]{EXPR_REF},
            (caller, args) -> Interpreter.evaluate(caller, null, (Expr) args[0])
    );
	
	public static final TypeRef BOOLEAN = new ByLayoutType(new EnumLayout("true", "false")).ref();

    public static final Scope GLOBAL = new Scope(null)
            .d("Expr", EXPR_REF)
            .d("Symbol", Symbol.TYPE)
            .d("Type", MetaType.of(0).ref())
			.d("WrappedExpr", WrappedExpr.TYPE.ref())
            .d("Blob", Func.intrinsic(TYPE_REF, new TypeRef[]{BLOB4_REF},
                    (caller, args) -> BlobType.of(((Blob) args[0]).toInt()).ref()))
			.d("Boolean", BOOLEAN)
            .d("blob4", Func.intrinsic(BLOB4_REF, new TypeRef[]{Symbol.TYPE},
                    (caller, args) -> Blob.ofInt(Integer.parseInt(((Symbol) args[0]).getValue()))))
            .d("blob1", Func.intrinsic(BlobType.of(1).ref(), new TypeRef[]{Symbol.TYPE},
                    (caller, args) -> new Blob(new byte[]{(byte) Integer.parseInt(((Symbol) args[0]).getValue())})))
            .d("macro", Func.intrinsic(null, new TypeRef[]{ExprList.TYPE(BracketsType.SQUARE), EXPR_REF},
                    (caller, args) -> macro((ExprList) args[0], (Expr) args[1])))
            .d("eval", evaluate)
            .d("fineval", evaluateFinale)
			.d("exprwrap", Func.intrinsic(WrappedExpr.TYPE.ref(), new TypeRef[]{EXPR_REF},
					(caller, args) -> new WrappedExpr((Expr) args[0])))
			.d("repldef", Func.intrinsic(WrappedExpr.TYPE.ref(), new TypeRef[]{WrappedExpr.TYPE.ref()},
					(caller, args) -> new WrappedExpr(replDef(caller, ((WrappedExpr) args[0]).getExpr()))))
            .d("fn", Func.intrinsic(null, new TypeRef[]{
                    ExprList.TYPE(BracketsType.SQUARE),
                    TYPE_REF,
                    EXPR_REF
            }, (caller, args) -> fn(caller, (ExprList) args[0], (TypeRef) args[1], (Expr) args[2])))
            .d("fnt", Func.intrinsic(TYPE_REF, new TypeRef[]{
                    ExprList.TYPE(BracketsType.SQUARE),
                    TYPE_REF
            }, (caller, args) -> fnt(caller, (ExprList) args[0], (TypeRef) args[1]).ref()))
            .d("+", Func.intrinsic(arithmeticFnType, (caller, args) -> plus(args)))
            .d("*", Func.intrinsic(arithmeticFnType, (caller, args) -> multiply(args)))
            .d("-", Func.intrinsic(arithmeticFnType, (caller, args) -> minus(args)))
            .d("/", Func.intrinsic(arithmeticFnType, (caller, args) -> divide(args)))
            .d("baseType", Func.intrinsic(null, new TypeRef[]{TYPE_REF},
                    (caller, args) -> ((TypeRef) args[0]).getBaseType()))
            .d("typeOf", Func.intrinsic(TYPE_REF, new TypeRef[]{EXPR_REF},
                    (caller, args) -> typeOf(caller, (Expr) args[0])))
            .d("charextract", Func.intrinsic(null, new TypeRef[]{Symbol.TYPE},
                    (caller, args) -> charExtract((Symbol) args[0])))
            .d("::", Func.intrinsic(null, new TypeRef[]{Symbol.TYPE, EXPR_REF},
                    (caller, args) -> define(caller, ((Symbol) args[0]).getValue(), null, (Expr) args[1])))
            .d(":", Func.intrinsic(null, new TypeRef[]{TYPE_REF, Symbol.TYPE, EXPR_REF},
                    (caller, args) -> define(caller, ((Symbol) args[1]).getValue(), (TypeRef) args[0], (Expr) args[2])));

    public static Func macro(ExprList parameters, Expr body) {
        String[] paramNames = extractParamNames(parameters);
        TypeRef[] paramTypes = Stream.generate(() -> EXPR_REF)
                .limit(paramNames.length)
                .toArray(TypeRef[]::new);

        return new Func(
                new FunctionType(EXPR_REF, paramTypes),
                (c, a) -> replaceDefined(body, a, paramNames)
        );
    }

    private static String[] extractParamNames(ExprList parameters) {
        return parameters.getElements().stream()
                .map(e -> ((Symbol) e).getValue())
                .toArray(String[]::new);
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

    private static Blob arithReduce(Val[] args, BinaryOperator<Integer> operator) {
        return Blob.ofInt(Arrays.stream(args)
                .skip(1)
                .map(arg -> ((Blob) arg).toInt())
                .reduce(((Blob) args[0]).toInt(), operator));
    }

    private static Expr replaceDefined(Expr e, Val[] args, String[] names) {
        if (e instanceof ExprList) {
            List<Expr> elements = ((ExprList) e).getElements();
            elements.replaceAll(syntaxElement -> replaceDefined(syntaxElement, args, names));
            return new ExprList(e.getLocation(), ((ExprList) e).getBracketsType(), elements);
        }
        if (e instanceof Symbol) {
            for (int i = 0; i < names.length; i++) {
                if (((Symbol) e).getValue().equals(names[i])) {
                    return (Expr) args[i];
                }
            }
        }
        return e;
    }
	private static Expr replDef(Scope scope, Expr e) {
		if (e instanceof ExprList) {
			List<Expr> elements = ((ExprList) e).getElements();
			elements.replaceAll(syntaxElement -> replDef(scope, syntaxElement));
			return new ExprList(e.getLocation(), ((ExprList) e).getBracketsType(), elements);
		}
		if (e instanceof Symbol) {
			Val r = scope.resolve(((Symbol) e).getValue());
			if (r instanceof Expr) {
				return (Expr) r;
			}
		}
		return e;
	}

    public static Val define(Scope scope, String name, TypeRef expectedType, Expr expr) {
        Val value = Interpreter.evaluate(scope, expectedType, expr);
        scope.getParent().define(name, value);
        return value;
    }

    public static TypeRef typeOf(Scope scope, Expr expr) {
        try {
            return Interpreter.evaluateFinale(scope, null, expr).getType();
        } catch (NullPointerException e) {
            return expr.getType();
        }
    }

    public static Func fn(Scope scope, ExprList parameters, TypeRef returnType, Expr body) {
        TypeRef[] paramTypes = new TypeRef[parameters.size()];
        String[] paramNames = new String[parameters.size()];
        
        parameters.getElements().stream()
                .map(p -> (ExprList) p)
                .forEach(p -> {
                    int i = Arrays.asList(paramNames).indexOf(null);
                    paramTypes[i] = (TypeRef) Interpreter.evaluate(scope, TYPE_REF, p.get(0));
                    paramNames[i] = ((Symbol) p.get(1)).getValue();
                });

        return new Func(
                new FunctionType(returnType, paramTypes),
                (c, a) -> {
                    for (int i = 0; i < a.length; i++) {
                        c.define(paramNames[i], a[i]);
                    }
                    return Interpreter.evaluate(c, returnType, body);
                }
        );
    }

    public static FunctionType fnt(Scope scope, ExprList parameters, TypeRef returnType) {
        TypeRef[] paramTypes = parameters.getElements().stream()
                .map(expr -> (TypeRef) Interpreter.evaluate(scope, TYPE_REF, expr))
                .toArray(TypeRef[]::new);
        return new FunctionType(returnType, paramTypes);
    }

    public static Blob charExtract(Symbol arg) {
        char[] v = arg.getValue().toCharArray();
        int Len = v.length;
        byte[] d = new byte[Len];
        for (int i = 0; i < Len; i++) {
            d[i] = (byte) v[i];
        }
        return new Blob(d);
    }
	
	
	public static int squareSum(int[] n) {
		return Arrays.stream(n).map(i -> i*i).sum();
	}
}
