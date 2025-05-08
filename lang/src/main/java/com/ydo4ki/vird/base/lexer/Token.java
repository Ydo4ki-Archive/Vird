package com.ydo4ki.vird.base.lexer;

import com.ydo4ki.vird.base.Location;

import java.io.File;

public class Token {
	public final TokenType type;
	public final String text;
	public final Location location;

	public Token(TokenType type, String text, int startpos, int endpos, int line, File file) {
		this.type = type;
		this.text = text;
		this.location = new Location(startpos,endpos,line,line,file);
	}
	public Token(TokenType type,int startpos,int endpos, int line, File file) {
		this(type,"\0",startpos,endpos,line,file);
	}

	@Override
	public String toString() {
		return type + "(" +
			(text != null ?"'" + text + '\'' : "")+
			", startpos=" + location.getStartPos() +
			", endpos=" + location.getEndPos() +
			", line=" + location.getStartLine() +
			')';
	}
}
