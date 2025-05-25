package com.ydo4ki.vird.base;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Sulphuris
 * @since 4/11/2025 1:04 AM
 */
@Data
public final class Location {
	private final int startPos;
	private final int endPos;
	private final int startLine;
	private final int endLine;
	private final File sourceFile;
	
	public static Location between(Location start, Location end) {
		if (!end.getSourceFile().equals(start.getSourceFile()))
			return start;
		return new Location(start.startPos,end.endPos,start.startLine,end.endLine, end.getSourceFile());
	}
}