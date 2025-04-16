package com.ydo4ki.vird;

import com.ydo4ki.vird.lexer.Lexer;
import com.ydo4ki.vird.lexer.Token;
import com.ydo4ki.vird.lexer.UnexpectedTokenException;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Sulphuris
 * @since 4/16/2025 2:13 PM
 */
public class LexerTest {
	public static void main(String[] args) throws IOException {
		File src = new File("vird/file2.vird");
		String source = String.join("\n", Files.readAllLines(src.toPath()));
		try {
			val t = new Lexer().tokenize(source, src);
			for (Token token : t) {
				System.out.println(token);
			}
		} catch (UnexpectedTokenException e) {
			throw new RuntimeException(e);
		}
	}
}
