package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
	
	
	public static void main(String[] __args) throws IOException {
//		DList program = (DList) new Parser().read(null, "(include 'brougham/source.bham')");
//		System.out.println(program);
		Interpreter interpreter = new Interpreter();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				Val ret = interpreter.next(in);
				System.out.println(ret);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}
}
