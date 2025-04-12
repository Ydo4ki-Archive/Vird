package com.ydo4ki.brougham.lib;

import com.ydo4ki.brougham.Interpreter;
import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.lang.*;

import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/12/2025 4:20 PM
 */
public final class Std {
	private Std() {}
	
	public static final FunctionImpl evaluate = new FunctionImpl(
			new FunctionType(
					null,
					new TypeRef[]{
							SyntaxElementType.INSTANCE.ref(),
					}
			),
			(caller, args) -> Interpreter.evaluate(caller, null, args[0]),
			true
	);
	
	public static void setup(Scope scope) {
		TypeRef blob4 = scope.define("Blob4", BlobType.of(4).ref());
//		ConversionRule.ConversionTypes conversionTypes = new ConversionRule.ConversionTypes(blob4, Symbol.TYPE);
//		scope.defineConversionRule(new ConversionRule(conversionTypes, blob4.getFunctionBySignature(conversionTypes.toFunctionType())));
		
		scope.define("define",
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{
										Symbol.TYPE,
										SyntaxElementType.INSTANCE.ref()
								}
						),
						(caller, args) -> {
							Val value = Interpreter.evaluate(caller, null, args[1]);
							caller.getParent().define(((Symbol)args[0]).getValue(), value);
							return value;
						},
						false
				)
				
				);
		scope.define("function",
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{
										DList.TYPE(BracketsType.SQUARE),
										new Symbol(new Location(null, 0, 0), scope, ":").getType(),
										TypeRefType.instance.ref(),
										DList.TYPE(BracketsType.ROUND),
								}
						),
						(caller, args) -> {
							System.out.println(Arrays.toString(args));
							return new Blob(new byte[10]);
						}, false
				)
		);
		scope.define("evaluate",evaluate);
		scope.define("+",
				new FunctionImpl(
						new FunctionType(
								BlobType.of(4).ref(),
								new TypeRef[]{
										BlobType.of(4).ref(),
										BlobType.of(4).ref(),
										BlobType.of(4).vararg(),
								}
						),
						(caller, args) -> {
							int sum = 0;
							for (Val arg : args) {
								sum += ((Blob)arg).toInt();
							}
							return Blob.ofInt(sum);
						}
						, true
				)
		);
		scope.define("typeOf",
				new FunctionImpl(
						new FunctionType(
								TypeRefType.instance.ref(),
								new TypeRef[]{
										SyntaxElementType.INSTANCE.ref()
								}
						),
						(caller, args) -> {
							Val evaluated;
							try {
								evaluated = Interpreter.evaluate(caller, null, args[0]);
							} catch (NullPointerException e) {
								evaluated = args[0];
							}
							return evaluated.getType();
						}
						, true
				)
		);
	}
}
