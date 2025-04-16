package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.expr.Expr;
import com.ydo4ki.vird.lang.expr.ExprList;
import com.ydo4ki.vird.lang.expr.Symbol;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//
///**
// * @since 4/6/2025 8:40 PM
// * @author Sulphuris
// */
//public class Parser {
//	private char ch;
//
//	private char next(Source in) throws IOException {
//		ch = in.read();
//		if (ch == ';') while (ch != '\n') ch = next(in);
//		return ch;
//	}
//
//	private Symbol parseSymbol(Source in) throws IOException {
//		StringBuilder token = new StringBuilder();
//		int start = in.getCursor();
//		if (ch == '"') {
//			boolean skipNext = false;
//			token.append(ch);
//			ch = next(in);
//			while (ch != '"' || skipNext) {
//				if (ch == '\\') {
//					skipNext = true;
//				} else {
//					if (ch != '"' && skipNext) token.append("\\");
//					token.append(ch);
//					skipNext = false;
//				}
//				ch = next(in);
//				if (ch == 0xFFFF) return null;
//			}
//			ch = next(in);
//			if (ch == 0xFFFF) return null;
//			token.append('"');
//		} else {
//			if (delimiter_operators.contains(String.valueOf(ch))) {
//				token.append(ch);
//				ch = next(in);
//			} else while (!Character.isWhitespace(ch) && !delimiter_operators.contains(String.valueOf(ch))) {
//				if (ch == 0xFFFF) return null;
//				token.append(ch);
//				ch = next(in);
//			}
//		}
//		return new Symbol(new Location(in, start, in.getCursor()), token.toString());
//	}
//
//	private ExprList parseDList(BracketsType bracketsType, Source in) throws IOException {
//		List<Expr> elements = new ArrayList<>();
//		int start = in.getCursor()-3;
//		ExprList exprList =  new ExprList(bracketsType, elements);
//		Expr next;
//		while (true) {
//			next = parseVal(bracketsType, in);
//			if (next == null) break;
//			elements.add(next);
//		}
//		exprList.setLocation(new Location(in, start, in.getCursor()-1));
//		ch = next(in);
//		return exprList;
//	}
//
//	private Expr parseVal(BracketsType brackets, Source in) throws IOException {
//		if (ch == 0xFFFF) return null;
//		while (Character.isWhitespace(ch)) {
//			ch = next(in);
//			if (ch == 0xFFFF) return null;
//		}
//		if (brackets != null && ch == brackets.close) {
//			return null;
//		}
//		if (ch == '['|| ch == '(' || ch == '{') {
//			char ch = this.ch;
//			this.ch = next(in);
//			return parseDList(BracketsType.byOpen(ch), in);
//		}
//		if (ch == ']' || ch == ')' || ch == '}') {
//			throw new IllegalArgumentException("Unexpected bracket: " + ch);
//		}
//		return parseSymbol(in);
//	}
//
//	private static final String delimiter_operators = ",[]{}()";
//
//	public Expr read(String program) throws IOException {
//		Source in = new Source.OfString(program);
//		Expr ret = read(in);
//		in.close();
//		return ret;
//	}
//	public Expr read(Source in) throws IOException {
//		ch = next(in);
//		return parseVal(BracketsType.ROUND, in);
//	}
//}
