package com.ydo4ki.vird.base.lexer;

import com.ydo4ki.vird.base.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Sulphuris
 * @since 4/16/2025 7:50 PM
 */
public class ExprOutput implements Iterable<Expr> {
	private final TokenOutput tokenOutput;

    public ExprOutput(TokenOutput tokenOutput) {
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
			if (isEOF() || currentToken.text.isEmpty()) return false;
			return currentToken.text.charAt(0) == type.close;
		}
		
		private BracketsType getBracketType() {
			if (currentToken.type == TokenType.OPEN) {
				return tokenOutput.getBracketsTypes().byOpen(currentToken.text.charAt(0));
			}
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
			
			while (!isEOF() && !isMatchingCloseBracket(bracketsType)) {
				Expr next = parseExpr(bracketsType);
				if (next == null) break;
				elements.add(next);
			}
			
			ExprList exprList = ExprList.of(Location.between(startToken.location, currentToken.location), bracketsType, elements);
			
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
			if (currentToken.type == TokenType.CLOSE) {
				throw new IllegalArgumentException("Unexpected bracket: " + currentToken.text);
			}
			
			return parseSymbol();
		}
    }
}
