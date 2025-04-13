package com.ydo4ki.brougham.lib;

import com.ydo4ki.brougham.Interpreter;
import com.ydo4ki.brougham.Location;
import com.ydo4ki.brougham.lang.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/12/2025 4:20 PM
 */
public final class Std {
	private Std() {
	}
	
	public static final Func evaluate = new Func(
			new FunctionType(
					null,
					new TypeRef[]{
							SyntaxElement.TYPE.ref(),
					}
			),
			(caller, args) -> Interpreter.evaluate(caller, null, (SyntaxElement) args[0]),
			true
	);
	
	public static void setup(Scope scope) {
		TypeRef blob4 = scope.define("Blob4", BlobType.of(4).ref());
//		ConversionRule.ConversionTypes conversionTypes = new ConversionRule.ConversionTypes(blob4, Symbol.TYPE);
//		scope.defineConversionRule(new ConversionRule(conversionTypes, blob4.getFunctionBySignature(conversionTypes.toFunctionType())));
		scope.define("charextract",
				Func.intrinsic(null, new TypeRef[]{Symbol.TYPE}, true,
						(caller, args) -> {
							return new Blob(((Symbol) args[0]).getValue().getBytes(StandardCharsets.UTF_8));
						}
				)
		);
		scope.define("blob4",
				Func.intrinsic(BlobType.of(4).ref(), new TypeRef[]{Symbol.TYPE}, true,
						(caller, args) -> {
							return Blob.ofInt(Integer.parseInt(((Symbol) args[0]).getValue()));
						}
				)
		);
		
		scope.define("define",
				Func.intrinsic(null, new TypeRef[]{Symbol.TYPE, SyntaxElement.TYPE.ref()}, false,
						(caller, args) -> {
							Val value = Interpreter.evaluate(caller, null, (SyntaxElement) args[1]);
							caller.getParent().define(((Symbol) args[0]).getValue(), value);
							return value;
						}
				)
		);
		scope.define("fn",
				Func.intrinsic(null, new TypeRef[]{
								DList.TYPE(BracketsType.SQUARE),
								new Symbol(new Location(null, 0, 0), ":").getType(),
								TypeRef.TYPE.ref(),
								DList.TYPE(BracketsType.ROUND),
						}, false,
						(caller, args) -> {
							DList parameters = ((DList) args[0]);
							TypeRef returnType = (TypeRef) args[2];
							DList body = (DList) args[3];
							
							TypeRef[] paramTypes = new TypeRef[parameters.getElements().size()];
							String[] paramNames = new String[parameters.getElements().size()];
							int i = 0;
							for (Val element : parameters.getElements()) {
								DList p = (DList) element;
								paramTypes[i] = (TypeRef) Interpreter.evaluate(caller, TypeRef.TYPE.ref(), p.getElements().get(0));
								paramNames[i] = ((Symbol) p.getElements().get(1)).getValue();
								i++;
							}
							FunctionType functionType = new FunctionType(
									returnType,
									paramTypes
							);
							SyntaxElement functionId = body.getElements().get(0);
							Val function = Interpreter.evaluate(caller, null, functionId);
							
							return new Func(functionType,
									(c, a) -> {
										for (int i1 = 0; i1 < a.length; i1++) {
											c.define(paramNames[i1], a[i1]);
										}
										return Interpreter.evaluate(c, returnType, body);
									},
									((Func) function).isPure()
							);
						}
				)
		);
		scope.define("evaluate", evaluate);
		scope.define("+",
				Func.intrinsic(BlobType.of(4).ref(), new TypeRef[]{
								BlobType.of(4).ref(),
								BlobType.of(4).ref(),
								BlobType.of(4).vararg(),
						}, true,
						(caller, args) -> {
							int sum = 0;
							for (Val arg : args) {
								sum += ((Blob) arg).toInt();
							}
							return Blob.ofInt(sum);
						}
				)
		);
		scope.define("typeOf",
				Func.intrinsic(TypeRef.TYPE.ref(), new TypeRef[]{SyntaxElement.TYPE.ref()}, true,
						(caller, args) -> {
							Val evaluated;
							try {
								evaluated = Interpreter.evaluate(caller, null, (SyntaxElement) args[0]);
							} catch (NullPointerException e) {
								evaluated = args[0];
							}
							return evaluated.getType();
						}
				)
		);
	}
}
