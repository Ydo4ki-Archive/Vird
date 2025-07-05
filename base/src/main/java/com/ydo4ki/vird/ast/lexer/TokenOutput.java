package com.ydo4ki.vird.ast.lexer;

import com.ydo4ki.vird.ast.BracketsType;
import com.ydo4ki.vird.ast.BracketsTypes;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TokenOutput implements Iterable<Token> {
	
	private final String source;
	private final File file;
	@Getter
	private final BracketsTypes bracketsTypes;
	
	public TokenOutput(String source, File file, BracketsTypes bracketsTypes) {
		this.source = source;
		this.file = file;
		this.bracketsTypes = bracketsTypes;
	}
	public TokenOutput(Path file, BracketsTypes bracketsTypes) throws IOException {
		this(String.join("\n", Files.readAllLines(file)), file.toFile(), bracketsTypes);
	}
	public TokenOutput(File file, BracketsTypes bracketsTypes) throws IOException {
		this(String.join("\n", Files.readAllLines(file.toPath())), file, bracketsTypes);
	}
	
	@Override
	public Iterator<Token> iterator() {
		return new TokenIterator();
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
			
			// skip whitespace
			while (Character.isWhitespace(ch)) {
				if (ch == '\n') line++;
				ch = nextChar();
			}
			
			if (ch == '\0')
				return new Token(TokenType.EOF, pos - 1, pos, line, file);
			
			
			// brackets
			{
				TokenType type;
				BracketsType bracketsType = bracketsTypes.byOpen(ch);
				if (bracketsType == null) {
					bracketsType = bracketsTypes.byClose(ch);
					type = TokenType.CLOSE;
				} else type = TokenType.OPEN;
				if (bracketsType != null) return new Token(type, String.valueOf(ch), pos - 1, pos, line, file);
			}
			
			// comments
			if (ch == '/') {
				Token comment = readComment(ch);
				if (comment != null) return comment;
			}
			
			// string literals
			if (ch == '"' || ch == '\'') {
				return readLiteral(ch);
			}
			
			// identifiers
			if (isValidNameChar(bracketsTypes, ch)) {
				return readIdentifier(ch);
			}
			
			exception = new Exception("I have no idea what this is: " + ch);
			return new Token(TokenType.ERROR, String.valueOf(ch), pos - 1, pos, line, file);
		}

		private Token readComment(char ch) {
			char next = seeNextChar();
			if (next != '/' && next != '*') {
				return null;
			}
			
			int startpos = pos - 1;
			StringBuilder builder = new StringBuilder();
			boolean isLineComment = nextChar() == '/';
			
			if (isLineComment) {
				readUntil('\n', builder);
				pos--;
			} else {
				readMultilineComment(builder);
				pos++;
			}
			
			return new Token(TokenType.COMMENT, builder.toString(), startpos, pos, line, file);
		}
		
		private void readUntil(char end, StringBuilder builder) {
			char ch;
			while ((ch = nextChar()) != end && ch != '\0') {
				builder.append(ch);
			}
		}
		
		private void readMultilineComment(StringBuilder builder) {
			char ch;
			while (true) {
				ch = nextChar();
				if (ch == '*' && seeNextChar() == '/') {
					break;
				}
				if (ch == '\n') {
					line++;
				}
				builder.append(ch);
			}
		}

		private Token readLiteral(char ch) {
			int startpos = pos - 1;
			char separator = ch;
			StringBuilder builder = new StringBuilder();
			builder.append(separator);
			while ((ch = nextChar()) != separator) {
				if (ch == '\\') {
					ch = nextChar();
					builder.append('\\');
					if (ch == separator) {
						builder.setLength(builder.length() - 1);
					}
				}
				builder.append(ch);
			}
			builder.append(separator);
			return new Token(TokenType.STRING, builder.toString(), startpos, pos, line, file);
		}
		
		private Token readIdentifier(char ch) {
			int startpos = pos - 1;
			StringBuilder builder = new StringBuilder().append(ch);
			while (isValidNameChar(bracketsTypes, ch = nextChar())) {
				builder.append(ch);
			}
			pos--;
			return new Token(TokenType.IDENTIFIER, builder.toString(), startpos, pos, line, file);
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

	private static boolean isValidNameChar(BracketsTypes bracketsTypes, char ch) {
		return ch != '\0' && !Character.isWhitespace(ch) && !bracketsTypes.isBracket(ch);
	}
}
