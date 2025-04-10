package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.BracketsType;
import com.ydo4ki.brougham.lang.DList;
import com.ydo4ki.brougham.lang.Symbol;
import com.ydo4ki.brougham.lang.Val;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 4/6/2025 8:40 PM
 * @author Sulphuris
 */
public class Parser {
	private char ch;
	private char next_ch;
	
	private char next(BufferedReader in) throws IOException {
		ch = next_ch == 0 ? (char) in.read() : next_ch;
		next_ch = (char) in.read();
		
		if (ch == '/' && next_ch == '/') while (next_ch != '\n' && next_ch != 0xFFFF) ch = next(in);
		return ch;
	}
	
	private Symbol parseSymbol(DList parent, BufferedReader in) throws IOException {
		StringBuilder token = new StringBuilder();
		if (ch == '"') {
			boolean skipNext = false;
			token.append(ch);
			ch = next(in);
			while (ch != '"' || skipNext) {
				if (ch == '\\') {
					skipNext = true;
				} else {
					if (ch != '"' && skipNext) token.append("\\");
					token.append(ch);
					skipNext = false;
				}
				ch = next(in);
				if (ch == 0xFFFF) return null;
			}
			token.append('"');
		} else {
			if (delimiter_operators.contains(String.valueOf(ch))) {
				token.append(ch);
				ch = next(in);
			} else while (!Character.isWhitespace(ch) && !delimiter_operators.contains(String.valueOf(ch))) {
				if (ch == 0xFFFF) return null;
				token.append(ch);
				ch = next(in);
			}
		}
		return new Symbol(parent, token.toString());
	}
	
	private DList parseDList(DList parent, BracketsType bracketsType, BufferedReader in) throws IOException {
		List<Val> elements = new ArrayList<>();
		DList DList =  new DList(parent, bracketsType, elements);
		Val next;
		while (true) {
			next = parseVal(DList, bracketsType, in);
			if (next == null) break;
			elements.add(next);
		}
		ch = next(in);
		return DList;
	}
	
	private Val parseVal(DList parent, BracketsType brackets, BufferedReader in) throws IOException {
		if (ch == 0xFFFF) return null;
		while (Character.isWhitespace(ch)) {
			ch = next(in);
			if (ch == 0xFFFF) return null;
		}
		if (brackets != null && ch == brackets.close) {
			return null;
		}
		if (ch == '['|| ch == '(' || ch == '{') {
			char ch = this.ch;
			this.ch = next(in);
			return parseDList(parent, BracketsType.byOpen(ch), in);
		}
		if (ch == ']' || ch == ')' || ch == '}') {
			throw new IllegalArgumentException("Unexpected bracket: " + ch);
		}
		return parseSymbol(parent, in);
	}
	
	private static final String delimiter_operators = ";,[]{}()";
	
	public Val read(DList parent, File file) throws IOException {
		DList fileDList = new DList(parent, BracketsType.ROUND, new ArrayList<>());
		Symbol name = new Symbol(fileDList, file.getName());
		fileDList.getElements().add(name);
		File[] files = file.listFiles();
		if (files == null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
			Val DList = read(fileDList, in);
			in.close();
			fileDList.getElements().add(DList);
		} else {
			List<Val> elements = new ArrayList<>();
			DList group = new DList(fileDList, BracketsType.ROUND, elements);
			for (File file1 : files) {
				if (!file1.getName().endsWith(".bham")) continue;
				elements.add(read(group, file1));
			}
			fileDList.getElements().add(group);
		}
		return fileDList;
	}
	
	public Val read(String program) throws IOException {
		return read(null, program);
	}
	public Val read(DList parent, String program) throws IOException {
		BufferedReader in = new BufferedReader(new StringReader(program));
		Val ret = read(parent, in);
		in.close();
		return ret;
	}
	public Val read(DList parent, BufferedReader in) throws IOException {
		ch = next(in);
		return parseVal(parent, BracketsType.ROUND, in);
	}
}
