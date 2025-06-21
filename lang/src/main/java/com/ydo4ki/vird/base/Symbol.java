package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Type;
import com.ydo4ki.vird.lang.constraint.FreeConstraint;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 4/7/2025 10:33 PM
 * @author Sulphuris
 */
@Getter
public final class Symbol extends Expr {
	private final String value;
	
	public Symbol(Location location, String value) {
		super(location);
		this.value = value;
	}
	
	@Override
	public Collection<Symbol> split(String... separateLines) {
		String line = value;
		
		int lineLength = line.length();
		int start = 0;
		int current = 0;
		
		List<String> validSeparators = Arrays.stream(separateLines).filter(sep -> !sep.isEmpty()).collect(Collectors.toList());
		
		if (validSeparators.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Symbol> result = new ArrayList<>();
		while (current <= lineLength) {
			Symbol foundSep = null;
			int maxLen = 0;
			
			for (String sep : validSeparators) {
				if (line.startsWith(sep, current)) {
					if (sep.length() > maxLen) {
						maxLen = sep.length();
						foundSep = new Symbol(new Location(
								getLocation().getStartPos() + start,
								getLocation().getStartPos() + current,
								getLocation().getStartLine(),
								getLocation().getEndLine(),
								getLocation().getSourceFile()
						), sep);
					}
				}
			}
			
			if (foundSep != null) {
				if (current > start) {
					result.add(new Symbol(new Location(
									getLocation().getStartPos() + start,
									getLocation().getStartPos() + current,
									getLocation().getStartLine(),
									getLocation().getEndLine(),
									getLocation().getSourceFile()
							), line.substring(start, current))
					);
				}
				result.add(foundSep);
				start = current + foundSep.value.length();
				current = start;
			} else {
				current++;
			}
		}
		
		if (start < lineLength) {
			result.add(new Symbol(new Location(
					getLocation().getStartPos() + start,
					getLocation().getStartPos() + (line.length() - start),
					getLocation().getStartLine(),
					getLocation().getEndLine(),
					getLocation().getSourceFile()
			), line.substring(start)));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(value, symbol.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
	
	public static final Type TYPE = new Type(FreeConstraint.INSTANCE);
}
