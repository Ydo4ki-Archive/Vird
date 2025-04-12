package com.ydo4ki.brougham.lang;

/**
 * @author Sulphuris
 * @since 4/12/2025 11:08 AM
 */
public interface ConcreteFunction extends Val {
	@Override
	FunctionType getRawType();
	
	Func asFunctionImpl();
	
	FunctionCall makeCall(Scope caller, TypeRef expectedType, TypeRef[] argsTypes);
}
