package com.ydo4ki.vird.lexer;

import com.ydo4ki.vird.BracketsType;

import java.io.File;
import java.util.*;

public class Lexer {

	public Lexer() {
	}

	private File file;

	private String source = null;
	private int pos = 0;
	private int line = 1;

	private Exception exception = null;

	public synchronized Iterable<Token> tokenize(String source, File file) throws UnexpectedTokenException {
		this.source = source;
		this.file = file;
		pos = 0;
		line = 1;
		exception = null;

//		Stack<Token> tokens = new Stack<>();
//		Token token = nextToken();
//		try {
//			tokens.push(token);
//			while (token.type != TokenType.EOF) {
//				tokens.push(token = nextToken());
//				if (token.type == TokenType.ERROR)
//					throw new UnexpectedTokenException(token, "Invalid token", exception);
//			}
//		} catch (UnexpectedTokenException e) {
//			Token errorToken = e.getToken();
//			if (errorToken != null) {
//				if (errorToken.text != null) System.err.println(errorToken.text);
//				String str = source.substring(errorToken.location.getStartPos(), errorToken.location.getEndPos());
//				System.err.println("Invalid token at line: " + errorToken.location.getStartLine());
//				System.err.println(str);
//				for (int i = 0; i < str.length(); i++) System.err.print('~');
//				System.err.println();
//			}
//			throw e;
//		}

		return () -> new Iterator<Token>() {
			Token next = nextToken();
			
			@Override
			public boolean hasNext() {
				return next.type != TokenType.EOF;
			}
			
			@Override
			public Token next() {
				Token token = next;
				next = nextToken();
				return token;
			}
		};
	}

	private Token nextToken() {
		char ch = nextChar();

		// skip
		while (ch == ' ' || ch == '\n' || ch == '\t') {
			if (ch == '\n') line++;
			ch = nextChar();
		}

		if (ch == '\0')
			return new Token(TokenType.EOF, pos - 1, pos, line, file);

		// comments
		if (ch == '/') {
			if (seeNextChar() == '/') {
				ch = nextChar();
				int startpos = pos;
				ch = nextChar();
				StringBuilder builder = new StringBuilder();
				while (ch != '\n') {
					builder.append(ch);
					ch = nextChar();
				}
				pos--;
				return new Token(TokenType.COMMENT, builder.toString(), startpos, pos, line, file);
			} else if (seeNextChar() == '*') {
				int startpos = pos;
				ch = nextChar();
				StringBuilder builder = new StringBuilder();
				while (!(ch == '*' && seeNextChar() == '/')) {
					if (ch == '\n') line++;
					builder.append(ch);
					ch = nextChar();
				}
				pos++;
				return new Token(TokenType.COMMENT, builder.toString(), startpos, pos, line, file);
			} else {
				//exception = new Exception();
				//return new Token(TokenType.ERROR,pos-1,pos,line);
			}
		}


		// string literals
		Token stringToken = readLiteral('"', ch, TokenType.STRING);
		if (stringToken != null) return stringToken;

		// char literals
		Token charsToken = readLiteral('\'', ch, TokenType.CHARS);
		if (charsToken != null) return charsToken;

		// identifiers
		if (isValidNameChar(ch)) {
			int startpos = pos - 1;
			StringBuilder builder = new StringBuilder();
			while (isValidNameChar(ch)) {
				builder.append(ch);
				ch = nextChar();
			}
			pos--;
			String value = builder.toString();
			return new Token(TokenType.IDENTIFIER, value, startpos, pos, line, file);
		}

		TokenType type;
		switch (ch) {
			case '(':
				type = TokenType.OPENROUND;
				break;
			case ')':
				type = TokenType.CLOSEROUND;
				break;
			case '[':
				type = TokenType.OPENSQUARE;
				break;
			case ']':
				type = TokenType.CLOSESQUARE;
				break;
			case '{':
				type = TokenType.OPEN;
				break;
			case '}':
				type = TokenType.CLOSE;
				break;
			default:
				type = TokenType.ERROR;
				exception = new Exception();
		}
		return new Token(type, String.valueOf(ch), pos - 1, pos, line, file);
	}

	private Token readLiteral(char separators, char ch, TokenType type) {
		if (ch == separators) {
			int startpos = pos;
			ch = nextChar();
			StringBuilder builder = new StringBuilder();
			while (ch != separators) {
				if (ch == '\\') {
					ch = nextChar();
					if (ch != separators)
						builder.append("\\");
				}
				builder.append(ch);
				ch = nextChar();
			}
			return new Token(type, builder.toString(), startpos - 1, pos, line, file);
		}
		return null;
	}

	private char nextChar() {
		if (pos >= source.length()) {
			pos++;
			return '\0';
		}
		return source.charAt(pos++);
	}

	private char seeNextChar() {
		if (pos >= source.length()) return '\0';
		return source.charAt(pos);
	}

	private boolean isValidNameChar(char ch) {
		return ch != '\0' && !Character.isWhitespace(ch) && !BracketsType.isBracket(ch);
	}
}
