package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.ast.Location;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * @author Sulphuris
 * @since 4/16/2025 2:03 PM
 */
@Getter
public class LangException extends Exception {
	@Setter
	private Location location;
	private final String rawMessage;
	
	public LangException(Location location, String message) {
		super(message);
		this.location = location;
		this.rawMessage = message;
	}
	
	public LangException(Location location, String message, Throwable cause) {
		this(location, message, cause, message);
	}
	
	public LangException(Location location, String message, String rawMessage) {
		super(message);
		this.location = location;
		this.rawMessage = rawMessage;
	}
	
	public LangException(Location location, String message, Throwable cause, String rawMessage) {
		super(message, cause);
		this.location = location;
		this.rawMessage = rawMessage;
	}
	
	public LangException(Location location, Throwable cause, String rawMessage) {
		super(cause);
		this.location = location;
		this.rawMessage = rawMessage;
	}
	
	public String errName() {
		return "Error";
	}
	
	public void handle(PrintStream err) {
		String source = String.join("\n", this.getLocation().getSource());
		File file = getLocation().getSourceFile();
		
		String filename = "<unknown>";
		if (file != null) {
			filename = file.getAbsolutePath();
			filename = filename.substring(1).replaceAll("\\|/", ".");
		}
		err.println(getErrorDescription(this, filename, source));
		if (this.getCause() != this && this.getCause() instanceof LangException) {
			err.println("for:");
			err.println(getErrorDescription((LangException) this.getCause(), filename, source));
		}
	}
	
	private static String getErrorDescription(LangException e, String filename, String source) {
		StringBuilder msg = new StringBuilder(e.errName()).append(": ").append(e.getRawMessage()).append(" (")
				.append(filename);
		if (e.getLocation() != null) {
			msg.append(':').append(e.getLocation().getStartLine());
		}
		msg.append(')').append("\n\n");
		
		if (e.getLocation() != null) {
			String line;
			try {
				line = source.split("\n")[e.getLocation().getStartLine() - 1];
			} catch (ArrayIndexOutOfBoundsException ee) {
				line = " ";
			}
			
			int linePos = source.substring(0, e.getLocation().getStartPos()).lastIndexOf('\n');
			int errStart = e.getLocation().getStartPos() - linePos;
			int errEnd = e.getLocation().getEndPos() - linePos;
			
			msg.append(line).append('\n');
			
			char[] underline = new char[line.length()];
			for (int i = 0; i < underline.length; i++) {
				if (i >= errStart - 1 && i < errEnd - 1) underline[i] = '~';
				else if (line.charAt(i) == '\t') underline[i] = '\t';
				else underline[i] = ' ';
			}
			return msg.append(underline).append('\n').toString();
		}
		
		return msg.append('\n').toString();
	}
}
