package com.ydo4ki.brougham;

import java.io.*;
import java.nio.file.Files;

public class Main {
	
	public static void main(String[] args) throws IOException {
		File source = new File("source.bham");
		BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(source.toPath())));
		new Parser1().read(in);
		in.close();
	}
	
}
