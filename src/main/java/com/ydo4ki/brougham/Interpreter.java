package com.ydo4ki.brougham;

import com.ydo4ki.brougham.lang.*;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/10/2025 4:04 PM
 */
@Getter
public class Interpreter {
	private final Scope program = new Scope(null);
	
	public Interpreter() {
	
	}
	
	public Val next(String in) throws IOException {
		return evaluate(program, null, new Parser().read(program, new Source.OfString(in)));
	}
	
	public Val next(BufferedReader in) throws IOException {
		return evaluate(program, null, new Parser().read(program, new Source.Raw(in)));
	}
	
	private Val evaluate(Scope caller, TypeRef expectedType, Val val) {
		if (expectedType != null && expectedType.matches(caller, val)) return val;
		if (val instanceof DList) return evaluate_function(caller, expectedType, (DList) val);
		if (val instanceof Symbol) return resolve((Symbol) val);
		if (expectedType != null) {
			// maybe find a cast function... idk
		}
		return val;
	}
	
	private Val evaluate_function(Scope caller, TypeRef expectedType, DList f) {
		Val functionId = f.getElements().get(0);
		Val function = evaluate(caller, null, functionId);
		if (function == null) {
			f.getLocation().print(System.err);
			throw new ThisIsNotTheBookClubException("Function not found: " + functionId);
		}
		
		final Val[] args;
		{
			List<Val> args0 = new ArrayList<>(f.getElements());
			args0.remove(0);
			args = args0.toArray(new Val[0]);
		}
		FunctionCall call = null;
		if (function instanceof FunctionSet) {
			FunctionSet set = (FunctionSet) function;
			call = set.makeCall(f, expectedType, args);
			if (call == null) {
				f.getLocation().print(System.err);
				throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
			}
		} else if (function instanceof FunctionImpl) {
			call = function_call(f, (FunctionImpl) function, expectedType, args);
		}
		if (call == null) {
			f.getLocation().print(System.err);
			throw new ThisIsNotTheBookClubException("Cannot create function call: " + String.valueOf(function));
		}
		
		return call.invoke(f, args);
	}
	
	private Val resolve(Symbol name) {
		return name.getParent().resolve(name.getValue());
	}
	
	private FunctionCall function_call(TypeRef expectedType, Symbol name, Val[] args) {
		FunctionCall func = name.getParent().resolveFunctionImpl(name.getValue(), expectedType, args);
		if (func == null)
			throw new IllegalArgumentException("Function not found: " + name + " " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		return func;
	}
	
	private FunctionCall function_call(Scope caller, FunctionImpl function, TypeRef expectedType, Val[] args) {
		FunctionCall call = FunctionCall.makeCall(caller, function, expectedType, Arrays.stream(args).map(Val::getType).toArray(TypeRef[]::new));
		if (call == null) {
			throw new IllegalArgumentException("Function not found: " + Arrays.stream(args).map(Val::getType).collect(Collectors.toList()));
		}
		return call;
	}
}
