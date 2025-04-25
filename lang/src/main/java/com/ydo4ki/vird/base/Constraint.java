package com.ydo4ki.vird.base;

import com.ydo4ki.vird.lang.Scope;

/**
 * @since 4/18/2025 12:21 AM
 * @author Sulphuris
 */
public interface Constraint {
	boolean test(Scope scope, Val value);
	
	// checks if the current constraint implies other (f.e., x > 0 => x >= 0)
	boolean implies(Scope scope, Constraint other);
	
	// simplified check for cases where the value is unknown
	default boolean isSatisfiable(Scope scope) {
		return true; // feasible by default
	}
	
	// -1 if this is stricter
	// 0 if strictness is equal or not comparable
	// 1 if other is stricter
	
	// wait a sec
	// if constraint A implies constraint B
	// and constraint B doesn't imply constraint A
	// then constrain B is stricter
	// and if both of them imply each other or none of them imply another one they are equal/uncomparable
	// therefore overriding this method is damn useless
	// jeez how come i didn't think of this before

//	default int isStricterThan(Constraint o) {
//		return 0;
//	}
	
	// wait a sec
	// do we even need constraints as a separate class hierarchy
	// if constraint is just a functions that return boolean and has extra information what does that function implies
	// but we do need this information in a functions anyway to inference constrains properly
	
	// ok so how do i present this information it's just like implies but a bit more complicated due to different possible return types
	// maybe something like
	// implies if Return == X -> ...
	//		   if Return == Y -> ...
	// 		   etc.
	// so the old implications in this style would look like:
	// implies if Return == True -> ...
	//		   if Return == False -> Not (implies True)
	// but there is another problem that function can accept multiple arguments so we need to provide for this
	// and still "implies" is not a collection, but a Predicate<Constraint> (a.k.a. func(func) -> True|False)
	
	// implies for arg 0 if Return == X <Predicate>
	//					 if Return == Y <Predicate>
	// 		   for arg 1 if Return == X <Predicate>
	//					 if Return == Y <Predicate>
	
}
