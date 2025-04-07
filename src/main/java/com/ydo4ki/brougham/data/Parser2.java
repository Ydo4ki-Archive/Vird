package com.ydo4ki.brougham.data;

import com.ydo4ki.brougham.parser.BracketsType;
import com.ydo4ki.brougham.parser.Element;
import com.ydo4ki.brougham.parser.Group;
import com.ydo4ki.brougham.parser.Token;

import java.util.List;

/**
 * @since 4/7/2025 10:39 PM
 * @author Sulphuris
 */
public class Parser2 {
	private Val resolveGroup(Group group) {
		List<Element> elements = group.getElements();
		int len = elements.size();
		Val[] values = new Val[len];
		int i = 0;
		for (Element element : elements) {
			values[i++] = resolve(element);
		}
		if (group.getType() == BracketsType.ROUND) {
			return new Tuple(values);
		} else if (group.getType() == BracketsType.BRACES) {
			return new Vector(values);
		} else throw new UnsupportedOperationException(group.getType().toString());
	}
	
	private Val resolveToken(Token token) {
		return new Symbol(token.getValue());
	}
	
	public Val resolve(Element element) {
		if (element instanceof Group) return resolveGroup((Group) element);
		assert element instanceof Token;
		return resolveToken((Token)element);
	}
}
