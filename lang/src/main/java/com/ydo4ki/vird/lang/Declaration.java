package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Val;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 4/22/2025 9:20 AM
 * @author Sulphuris
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class Declaration extends Val {
	private final String name;
	private final Val value;
}
