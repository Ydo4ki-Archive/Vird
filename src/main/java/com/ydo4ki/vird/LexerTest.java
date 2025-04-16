package com.ydo4ki.vird;

import com.ydo4ki.vird.lexer.TokenOutput;
import com.ydo4ki.vird.lexer.Token;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
		System.out.println("--------------------");
		for (Token token : lexer) {
			System.out.println(token);
		}
	}
}
