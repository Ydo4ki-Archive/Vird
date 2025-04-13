package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lib.Std;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
			).d("macro", Func.intrinsic(null, new TypeRef[]{DList.TYPE(BracketsType.SQUARE), Expr.TYPE.ref()},
					(caller, args) -> {
						DList parameters = ((DList) args[0]);
						Expr body = (Expr) args[1];
						return Std.macro(parameters, body);
					})
			).d("eval", Std.evaluate)
			.d("fineval", Std.evaluateFinale)
			.d("fn",
					Func.intrinsic(null, new TypeRef[]{
									DList.TYPE(BracketsType.SQUARE),
									new Symbol(new Location(null, 0, 0), ":").getType(),
									TypeRef.TYPE.ref(),
									Expr.TYPE.ref(),
							},
							(caller, args) -> Std.fn(caller, ((DList) args[0]), (TypeRef) args[2], (Expr) args[3])
					)
			)
			
			.d("+",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> Std.plus(args))
			).d("*",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> Std.multiply(args))
			).d("-",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> Std.minus(args))
			).d("/",
					Func.intrinsic(arithmeticFnType,
							(caller, args) -> Std.divide(args))
			).d("baseType",
					Func.intrinsic(null, new TypeRef[]{TypeRef.TYPE.ref()},
							(caller, args) -> ((TypeRef) args[0]).getBaseType())
			).d("typeOf",
					Func.intrinsic(TypeRef.TYPE.ref(), new TypeRef[]{Expr.TYPE.ref()},
							(caller, args) -> Std.typeOf(caller, (Expr) args[0]))
			).d("charextract",
					Func.intrinsic(null, new TypeRef[]{Symbol.TYPE},
							(caller, args) -> Std.charExtract((Symbol)args[0])
					)
			).d("::",
					Func.intrinsic(null, new TypeRef[]{Symbol.TYPE, Expr.TYPE.ref()},
							(caller, args) ->
									Std.define(caller, ((Symbol) args[0]).getValue(), null, (Expr) args[1]))
			).d(":",
					Func.intrinsic(null, new TypeRef[]{TypeRef.TYPE.ref(), Symbol.TYPE, Expr.TYPE.ref()},
							(caller, args) ->
									Std.define(caller, ((Symbol) args[1]).getValue(), ((TypeRef) args[0]), (Expr) args[2]))
			)
			;
	
}
