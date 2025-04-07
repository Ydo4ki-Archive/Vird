package com.ydo4ki.brougham;

import java.io.*;
import java.nio.file.Files;

public class Main {
	public static void main(String[] args) throws IOException {
		File source = new File("brougham");
		Group group = new Parser1().read(null, source);
		group.execute();
	}
}
