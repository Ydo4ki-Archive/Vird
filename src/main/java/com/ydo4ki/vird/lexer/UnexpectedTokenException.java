package com.ydo4ki.vird.lexer;

import com.ydo4ki.vird.LangException;

public class UnexpectedTokenException extends LangException {
	private final Token token;

	public Token getToken() {
		return token;
	}

	public UnexpectedTokenException(Token token, String msg, Exception e) {
		super(token.location, token.toString() + " ("+msg+")",e,msg);
		this.token = token;
	}
	public UnexpectedTokenException(Token token, char expected) {
		this(token,"'" + expected + "' expected");
	}
	public UnexpectedTokenException(Token token, String msg) {
		super(token.location, token + " ("+msg+")",msg);
		this.token = token;
	}
	public UnexpectedTokenException(Token token) {
		super(token.location, String.valueOf(token));
		this.token = token;
	}
}
