package com.ydo4ki.vird;

import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.Val;
import com.ydo4ki.vird.lang.expr.Expr;
import com.ydo4ki.vird.lexer.ExprOutput;
import com.ydo4ki.vird.lexer.TokenOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for Vird language code snippets
 */
public class VirdTest {
    private Scope scope;

    @BeforeEach
    void setUp() throws IOException {
        scope = new Scope(Vird.GLOBAL);
		for (Expr expr : new ExprOutput(new TokenOutput(new File("vird/test/testinit.vird")))) {
			Interpreter.evaluateFinale(scope, null, expr);
		}
    }

    /**
     * Helper method to evaluate a Vird code snippet and return the result
     */
    private Val evaluateVird(String code) throws IOException {
        File tempFile = File.createTempFile("vird_test", ".vird");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), code.getBytes());

        Val lastResult = null;
        for (Expr expr : new ExprOutput(new TokenOutput(tempFile))) {
            lastResult = Interpreter.evaluateFinale(scope, null, expr);
        }
        return lastResult;
    }

    @Test
    void testSimpleAddition() throws IOException {
        Val result = evaluateVird("(+ (blob4 1) (blob4 2))");
        assertEquals("b00000003", result.toString());
    }

    @ParameterizedTest
    @MethodSource("provideArithmeticTestCases")
    void testArithmeticOperations(String code, String expected) throws IOException {
        Val result = evaluateVird(code);
        assertEquals(expected, result.toString());
    }

    private static Stream<Arguments> provideArithmeticTestCases() {
        return Stream.of(
            Arguments.of("(+ (blob4 1) (blob4 2))", "b00000003"),
            Arguments.of("(- (blob4 5) (blob4 3))", "b00000002"),
            Arguments.of("(* (blob4 4) (blob4 3))", "b0000000C"),
            Arguments.of("(/ (blob4 10) (blob4 2))", "b00000005")
        );
    }

    @Test
    void testVariableAssignment() throws IOException {
        Val result = evaluateVird(
            "(:: x (blob4 42))\n" +
            "(+ x (blob4 8))");
        assertEquals("b00000032", result.toString());
    }

    @Test
    void testFunctionDefinition() throws IOException {
        Val result = evaluateVird(
            "(:: add (fn [(Blob4 a) (Blob4 b)] Blob4 (+ a b)))\n" +
            "(add (blob4 5) (blob4 3))");
        assertEquals("b00000008", result.toString());
    }
}
