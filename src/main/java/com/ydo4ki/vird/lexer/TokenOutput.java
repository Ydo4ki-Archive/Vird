package com.ydo4ki.vird.lexer;

import com.ydo4ki.vird.BracketsType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TokenOutput implements Iterable<Token> {
	
	private final String source;
	private final File file;
	
	public TokenOutput(String source, File file) {
		this.source = source;
		this.file = file;
	}
	public TokenOutput(Path file) throws IOException {
		this(String.join("\n", Files.readAllLines(file)), file.toFile());
	}
	public TokenOutput(File file) throws IOException {
		this(String.join("\n", Files.readAllLines(file.toPath())), file);
	}
	
	private class TokenIterator implements Iterator<Token> {
		
		private int pos = 0;
		private int line = 1;
		
		private Exception exception = null;
		
		private Token next = nextToken();
		
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
	}
	
	@Override
	public Iterator<Token> iterator() {
		return new TokenIterator();
	}


	private static boolean isValidNameChar(char ch) {
		return ch != '\0' && !Character.isWhitespace(ch) && !BracketsType.isBracket(ch);
	}
}
