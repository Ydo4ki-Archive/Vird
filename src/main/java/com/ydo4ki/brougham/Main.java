package com.ydo4ki.brougham;

import com.ydo4ki.brougham.data.Parser2;
import com.ydo4ki.brougham.data.Val;
import com.ydo4ki.brougham.parser.Group;
import com.ydo4ki.brougham.parser.Parser1;

import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		File source = new File("brougham/source.bham");
		Group group = (Group) ((Group) new Parser1().read(null, source).getElements().get(1)).getElements().get(0);
		System.out.println(group);
		Val program = new Parser2().resolve(group);
		System.out.println(program);
		System.out.println(program.getType());
		System.out.println(program.getType().getType());
	}
}
