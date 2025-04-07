package com.ydo4ki.brougham;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 4/6/2025 8:40 PM
 * @author Sulphuris
 */
public class Parser1 {
	private char ch;
	
	private char next(BufferedReader in) throws IOException {
		return (char) in.read();
	}
	
	private Token parseToken(Group parent, BufferedReader in) throws IOException {
		boolean operator = operators.contains(String.valueOf(ch));
		StringBuilder token = new StringBuilder();
		while (!Character.isWhitespace(ch) && (operator == operators.contains(String.valueOf(ch)))) {
			token.append(ch);
			ch = next(in);
			if (ch == 0xFFFF) return null;
		}
		return new Token(parent, token.toString());
	}
	
	private Group parseGroup(Group parent, BracketsType bracketsType, BufferedReader in) throws IOException {
		List<Element> elements = new ArrayList<>();
		Group group =  new Group(parent, bracketsType, elements);
		Element next;
		while (true) {
			next = parseElement(group, bracketsType, in);
			if (next == null) break;
			elements.add(next);
		}
		ch = next(in);
		return group;
	}
	
	private Element parseElement(Group parent, BracketsType brackets, BufferedReader in) throws IOException {
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
			return parseGroup(parent, BracketsType.byOpen(ch), in);
		}
		if (ch == ']' || ch == ')' || ch == '}') {
			throw new IllegalArgumentException("Unexpected bracket: " + ch);
		}
		return parseToken(parent, in);
	}
	
	private static final String operators = "+-/*=!%^&*:,.|[]{}()";
	
	public Group read(Group parent, File file) throws IOException {
		Group fileGroup = new Group(parent, BracketsType.BRACES, new ArrayList<>());
		Token name = new Token(fileGroup, file.getName());
		fileGroup.getElements().add(name);
		File[] files = file.listFiles();
		if (files == null) {
			BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
			Group group = read(fileGroup, in);
			in.close();
			fileGroup.getElements().add(group);
		} else {
			List<Element> elements = new ArrayList<>();
			Group group = new Group(fileGroup, BracketsType.BRACES, elements);
			for (File file1 : files) {
				elements.add(read(group, file1));
			}
			fileGroup.getElements().add(group);
		}
		return fileGroup;
	}
	
	public Group read(Group parent, BufferedReader in) throws IOException {
		ch = next(in);
		return parseGroup(parent, BracketsType.BRACES, in);
	}
}
