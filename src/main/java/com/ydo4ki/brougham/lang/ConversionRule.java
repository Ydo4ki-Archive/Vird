package com.ydo4ki.brougham.lang;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

/**
 * @author Sulphuris
 * @since 4/12/2025 10:39 AM
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class ConversionRule {
	@Getter
	private final TypeRef targetType;
	@Getter
	private final TypeRef from;
	private final BiFunction<Scope, Val, Val> conversion;
}
