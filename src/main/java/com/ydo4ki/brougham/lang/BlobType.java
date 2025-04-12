package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

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
	
	private final int length;
	
	@Override
	public String toString() {
		return "Blob"+length;
	}
}
