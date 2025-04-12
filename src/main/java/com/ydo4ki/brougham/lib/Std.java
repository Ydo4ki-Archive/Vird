package com.ydo4ki.brougham.lib;

import com.ydo4ki.brougham.lang.*;

import java.util.Arrays;

/**
 * @author Sulphuris
 * @since 4/12/2025 4:20 PM
 */
public final class Std {
	private Std() {}
	
	public static void setup(Scope scope) {
		scope.define("Blob4", BlobType.of(4));
		
		scope.define("+",
				new FunctionImpl(
						new FunctionType(
								BlobType.of(4).ref(),
								new TypeRef[]{
										Symbol.TYPE,
										Symbol.TYPE
								}
						),
						(caller, args) -> {
							System.out.println(Arrays.toString(args));
							return Blob.ofInt(10);
						}
						, true
				)
		);
	}
}
