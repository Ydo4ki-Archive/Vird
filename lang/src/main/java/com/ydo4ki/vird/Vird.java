package com.ydo4ki.vird;

import com.ydo4ki.vird.base.BracketsType;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Symbol;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Vird {
	
	public static final Scope GLOBAL = new Scope(null);
	
    private Vird() {
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
	
    public static Blob charExtract(Expr arg) {
        char[] v = arg.toString().toCharArray();
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
