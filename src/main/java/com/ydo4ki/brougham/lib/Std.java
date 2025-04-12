package com.ydo4ki.brougham.lib;

import com.ydo4ki.brougham.Interpreter;
import com.ydo4ki.brougham.lang.*;

import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/12/2025 4:20 PM
 */
public final class Std {
	private Std() {}
	
	public static void setup(Scope scope) {
		Type blob4 = scope.define("Blob4", BlobType.of(4));
		ConversionRule.ConversionTypes conversionTypes = new ConversionRule.ConversionTypes(blob4.ref(), Symbol.TYPE);
		scope.defineConversionRule(new ConversionRule(conversionTypes, blob4.getFunctionBySignature(conversionTypes.toFunctionType())));
		
		scope.define("evaluate",
				new FunctionImpl(
						new FunctionType(
								null,
								new TypeRef[]{
										SyntaxElementType.INSTANCE.ref(),
								}
						),
						(caller, args) -> Interpreter.evaluate(caller, null, args[0]),
						true
				)
		);
		scope.define("+",
				new FunctionImpl(
						new FunctionType(
								BlobType.of(4).ref(),
								new TypeRef[]{
										BlobType.of(4).ref(),
										BlobType.of(4).ref()
								}
						),
						(caller, args) -> {
							System.out.println(Arrays.toString(args));
							return Blob.ofInt(((Blob)args[0]).toInt() + ((Blob)args[1]).toInt());
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
