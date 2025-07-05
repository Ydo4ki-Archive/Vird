package com.ydo4ki.vird.ast;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;


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
	@Getter(AccessLevel.NONE)
	private String source;
	
	public Location(int startPos, int endPos, int startLine, int endLine, File sourceFile) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.startLine = startLine;
		this.endLine = endLine;
		this.sourceFile = Objects.requireNonNull(sourceFile);
	}
	
	public Location(int startPos, int endPos, int startLine, int endLine, File sourceFile, String source) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.startLine = startLine;
		this.endLine = endLine;
		if (source == null && sourceFile == null) {
			throw new NullPointerException("Unknown source");
		}
		this.sourceFile = sourceFile;
		this.source = source;
	}
	
	public String getSource() {
		if (source != null) return source;
		try {
			return source = String.join("\n", Files.readAllLines(sourceFile.toPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Location between(Location start, Location end) {
		if (!Objects.equals(end.getSourceFile(), start.getSourceFile()))
			return start;
		return new Location(start.startPos,end.endPos,start.startLine,end.endLine, end.getSourceFile(), end.source);
	}
	
	public static Location unknown(File src, String source) {
		return new Location(0,0,0,0, src, source);
	}
	public static Location unknown(File src) {
		return new Location(0,0,0,0, src, null);
	}
}