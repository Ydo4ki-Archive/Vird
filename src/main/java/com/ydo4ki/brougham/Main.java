package com.ydo4ki.brougham;

import com.ydo4ki.brougham.data.*;
import com.ydo4ki.brougham.parser.Group;
import com.ydo4ki.brougham.parser.Parser1;

import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		File source = new File("brougham/source.bham");
		Group group = (Group) ((Group) new Parser1().read(null, source).getElements().get(1)).getElements().get(0);
		System.out.println(group);
		Val program = new Parser2().resolve(group);
		System.out.println(program);
		
		
		Type number = new Type() {
		
		};
		
		Function plus = new Function(
				new FunctionType(number, new Type[]{number, number}),
				new Symbol[]{new Symbol("a"), new Symbol("b")}
		);
		Context context = new Context() {
			@Override
			public Val resolveReference(Symbol symbol) {
				try {
					int value = Integer.parseInt(symbol.getValue());
					return new Blob(new byte[]{
							(byte) (value >>> 24),
							(byte) (value >>> 16),
							(byte) (value >>> 8),
							(byte) value});
				} catch (NumberFormatException ignored) {
				}
				return symbol.getValue().equals("plus") ? plus : null;
			}
		};
		System.out.println(test_evaluate(program, context));
	}
	
	static Val test_evaluate(Val program, Context context) {
		if (program instanceof Tuple) {
			Tuple t = (Tuple) program;
			Val functionName = t.getValues()[0];
		}
		return program;
	}
}
