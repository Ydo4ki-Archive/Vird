package com.ydo4ki.brougham;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/11/2025 12:47 AM
 */
public abstract class Source {
	private final BufferedReader in;
	private int cursor;
	
	protected Source(BufferedReader in) {
		this.in = in;
	}
	
	public int getCursor() {
		return cursor;
	}
	
	public void close() throws IOException {
		in.close();
	}
	
	public char read() throws IOException {
		cursor++;
		return (char)in.read();
	}
	
	public abstract void print(PrintStream out, int start, int end) throws IOException;
	
	public static class OfString extends Source {
		private final String src;
		
		public OfString(String src) {
			super(new BufferedReader(new StringReader(src)));
			this.src = src;
		}
		
		@Override
		public String toString() {
			return "<inline>";
		}
		
		@Override
		public void print(PrintStream out, int start, int end) throws IOException {
			out.println(src.substring(start, end));
		}
	}
	public static class OfFile extends Source {
		private final File file;
		protected OfFile(File file) throws IOException {
			super(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()))));
			this.file = file;
		}
		
		@Override
		public String toString() {
			return file.getName();
		}
		
		@Override
		public void print(PrintStream out, int start, int end) throws IOException {
			String lines = String.join("\n", Files.readAllLines(file.toPath()));
			out.println(lines.substring(start, end));
		}
	}
	
}
