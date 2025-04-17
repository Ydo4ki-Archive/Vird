package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.expr.Expr;
import com.ydo4ki.vird.lang.expr.Parser3;
import com.ydo4ki.vird.lexer.TokenOutput;
import com.ydo4ki.vird.lexer.Token;

import java.io.File;
import java.io.IOException;

import static com.ydo4ki.vird.Main.printPrjInfo;

/**
 * @author Sulphuris
 * @since 4/16/2025 2:13 PM
 */
public class LexerTest {
	public static void main(String[] args) throws IOException {
		printPrjInfo(System.out);
		File src = new File("vird/file2.vird");
		
		TokenOutput lexer = new TokenOutput(src);
		for (Token token : lexer) {
			System.out.println(token);
		}
		for (Expr token : new Parser3(lexer)) {
			System.out.println(token);
		}
	}
}
