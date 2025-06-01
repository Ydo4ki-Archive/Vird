package com.ydo4ki.vird.project;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @since 6/1/2025 11:30 PM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Stability {
	String value();
	
	String LOW = "low";
	String PROB = "prob";
	String DESIRE_REP = "desire_rep";
}