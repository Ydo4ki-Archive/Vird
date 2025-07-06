package com.ydo4ki.vird;

import java.io.File;

/**
 * @since 7/6/2025 8:47 AM
 * @author alignie
 */
public class VirdSrc {
	final String source;
	final File sourceFile;
	
	VirdSrc(String source, File sourceFile) {
		this.source = source;
		this.sourceFile = sourceFile;
	}
	
	public static VirdSrc fromString(String vird) {
		return new VirdSrc(vird, null);
	}
	public static VirdSrc fromFile(File file) {
		return new VirdSrc(null, file);
	}
}
