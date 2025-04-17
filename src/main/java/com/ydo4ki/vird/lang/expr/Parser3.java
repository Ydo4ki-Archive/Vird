package com.ydo4ki.vird.lang.expr;

import com.ydo4ki.vird.BracketsType;
import com.ydo4ki.vird.lexer.Token;
import com.ydo4ki.vird.lexer.TokenOutput;
import com.ydo4ki.vird.lexer.TokenType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sulphuris
 * @since 4/16/2025 7:50 PM
 */
public class Parser3 implements Iterable<Expr> {
	private final TokenOutput tokenOutput;

    public Parser3(TokenOutput tokenOutput) {
        this.tokenOutput = tokenOutput;
    }
	
    @Override
    public Iterator<Expr> iterator() {
        return new ExprIterator();
    }

    private class ExprIterator implements Iterator<Expr> {
		private Expr next;
		
		private Token currentToken;
		private final Iterator<Token> tokenIterator;
		
		ExprIterator() {
			this.tokenIterator = tokenOutput.iterator();
			nextToken();
			next = parseExpr(null);
		}
        
        @Override
        public boolean hasNext() {
            return next != null;
        }
        
        @Override
        public Expr next() {
            Expr token = next;
            next = parseExpr(null);
            return token;
        }
		
		
		private boolean isEOF() {
			return currentToken == null || currentToken.type == TokenType.EOF;
		}
		
		private boolean isMatchingCloseBracket(BracketsType type) {
			if (isEOF()) return false;
			if (type == BracketsType.ROUND) return currentToken.type == TokenType.CLOSEROUND;
			if (type == BracketsType.SQUARE) return currentToken.type == TokenType.CLOSESQUARE;
			if (type == BracketsType.BRACES) return currentToken.type == TokenType.CLOSE;
			return false;
		}
		
		private BracketsType getBracketType() {
			if (currentToken.type == TokenType.OPENROUND) return BracketsType.ROUND;
			if (currentToken.type == TokenType.OPENSQUARE) return BracketsType.SQUARE;
			if (currentToken.type == TokenType.OPEN) return BracketsType.BRACES;
			return null;
		}
		
		private void nextToken() {
			while (tokenIterator.hasNext()) {
				currentToken = tokenIterator.next();
				if (currentToken.type != TokenType.COMMENT) {
					return;
				}
			}
			currentToken = null;
		}
		
		private Symbol parseSymbol() {
			if (isEOF()) return null;
			Token token = currentToken;
			nextToken();
			return new Symbol(token.location, token.text);
		}
		
		private ExprList parseDList(BracketsType bracketsType) {
			Token startToken = currentToken;
			List<Expr> elements = new ArrayList<>();
			nextToken(); // skip opening bracket
			
			ExprList exprList = new ExprList(startToken.location, bracketsType, elements);
			
			while (!isEOF() && !isMatchingCloseBracket(bracketsType)) {
				Expr next = parseExpr(bracketsType);
				if (next == null) break;
				elements.add(next);
			}
			
			nextToken(); // skip closing bracket
			return exprList;
		}
		
		private Expr parseExpr(BracketsType brackets) {
			if (isEOF()) return null;
			if (brackets != null && isMatchingCloseBracket(brackets)) return null;
			
			BracketsType bracketType = getBracketType();
			if (bracketType != null) {
				return parseDList(bracketType);
			}
			
			// Handle closing brackets in wrong context
			assert currentToken != null;
			if (currentToken.type == TokenType.CLOSEROUND ||
					currentToken.type == TokenType.CLOSESQUARE ||
					currentToken.type == TokenType.CLOSE) {
				throw new IllegalArgumentException("Unexpected bracket: " + currentToken.text);
			}
			
			return parseSymbol();
		}
    }
}
