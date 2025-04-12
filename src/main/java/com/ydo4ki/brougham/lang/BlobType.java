package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class BlobType extends Type {
	
	private static final ArrayList<BlobType> types = new ArrayList<>();
	
	public static BlobType of(int length) {
		while (length >= types.size()) {
			types.add(new BlobType(types.size()));
		}
		return types.get(length);
	}
	
	{
		addImpl(new FunctionImpl(
				new FunctionType(
						this.ref(),
						new TypeRef[]{Symbol.TYPE}
				),
				(caller, args) -> {
					return Blob.ofInt(Integer.parseInt(((Symbol)args[0]).getValue()));
				}, true
		));
	}
	
	private final int length;
	
	@Override
	public String toString() {
		return "Blob"+length;
	}
}
