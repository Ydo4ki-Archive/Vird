package com.ydo4ki.vird.base;

import lombok.Getter;
import lombok.Setter;

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
		super(message, cause);
		this.location = location;
		this.rawMessage = message;
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
}
