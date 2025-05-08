package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.Interpreter;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Symbol;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Blob;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;
import lombok.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 5/8/2025 11:40 PM
 * @author Sulphuris
 */
public final class Functional {
	
	/* meta */
	
	
	public static final Val define = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList.Round f) throws LangValidationException {
			Expr[] args = Interpreter.args(f);
			
			if (args.length != 2)
				throw new LangValidationException(f.getLocation(), "2 arguments expected");
			
			
			// todo: computed names
			if (!(args[0] instanceof Symbol))
				throw new LangValidationException(args[0].getLocation(), "Symbol expected (" + args[0] + ")");
			
			String name = ((Symbol) args[0]).getValue();
			ValidatedValCall value = Interpreter.evaluateValCall(caller, args[1]);
			Scope scope = caller.getParent();
			scope.predefine(f.getLocation(), name, value);
			return new ValidatedValCall(value.getConstraint()) {
				@Override
				public Val invoke0() {
					return scope.define(name);
				}
			};
		}
	};
	
	/* functional */
	
	public static final Val echo = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList.Round f) throws LangValidationException {
			if (f.size() != 2) throw new LangValidationException(f.getLocation(), "1 argument expected");
			Expr arg = f.get(1);
			if (arg instanceof Symbol) {
				String v = ((Symbol) arg).getValue();
				int lastChar = v.length() - 1;
				if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(lastChar) == '"') {
					String content = v.substring(1, lastChar);
					return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
						@Override
						public Val invoke0() {
							System.out.println(content);
							return Val.unit;
						}
					};
				}
			}
			ValidatedValCall call = Interpreter.evaluateValCall(caller, arg);
			return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
				@Override
				public @NonNull Val invoke0() {
					System.out.println(call.invoke());
					return Val.unit;
				}
			};
		}
		
		@Override
		public String toString() {
			return "echo";
		}
	};
	
	public static final Val sum = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList.Round f) throws LangValidationException {
			Expr[] args = Interpreter.args(f);
			
			if (args.length < 2)
				throw new LangValidationException(f.getLocation(), "2 or more args expected");
			
			BigInteger sumOfKnownValues = BigInteger.ZERO;
			List<ValidatedValCall> leftToEvaluate = new ArrayList<>();
			
			for (int i = 0, Len = args.length; i < Len; i++) {
				ValidatedValCall c = Interpreter.evaluateValCall(new Scope(caller), args[i]);
				if (c.isPure()) {
					sumOfKnownValues = sumOfKnownValues.add(((Blob)c.invoke()).bigInteger());
				} else {
					if (!c.getConstraint().implies(caller, new InstanceOfConstraint(Blob.class)))
						throw new LangValidationException(f.getLocation(), "Number expected (" + i + ")");
					leftToEvaluate.add(c);
				}
			}
			final BigInteger sokv = sumOfKnownValues;
			if (leftToEvaluate.isEmpty()) return ValidatedValCall.promiseVal(new Blob(sokv));
			return new ValidatedValCall(new InstanceOfConstraint(Blob.class)) {
				@Override
				public Val invoke0() {
					BigInteger sum = sokv;
					for (ValidatedValCall arg : leftToEvaluate) sum = sum.add(((Blob) arg.invoke()).bigInteger());
					return new Blob(sum.toByteArray());
				}
			};
		}
	};
	
	
	
	public static final Scope scope = new Scope(null)
			.push("sum", sum)
			.push(":", define)
			.push("echo", echo);
			;
			
}
