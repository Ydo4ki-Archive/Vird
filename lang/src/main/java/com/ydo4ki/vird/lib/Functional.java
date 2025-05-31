package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.Interpreter;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.Blob;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.ComparisonConstraint;
import com.ydo4ki.vird.lang.constraint.EqualityConstraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;
import lombok.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @since 5/8/2025 11:40 PM
 * @author Sulphuris
 */
public final class Functional {
	
	/* meta */
	
	
	public static final Val define = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (f.getBracketsType() != BracketsType.ROUND) return super.invocation(caller, f);
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
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (f.getBracketsType() != BracketsType.ROUND) return super.invocation(caller, f);
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
	
	public static final Val byteSize = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (f.getBracketsType() != BracketsType.ROUND) return super.invocation(caller, f);
			Expr[] args = Interpreter.args(f);
			
			if (args.length != 1)
				throw new LangValidationException(f.getLocation(), "1 argument expected");
			
			ValidatedValCall c = Interpreter.evaluateValCall(new Scope(caller), args[0]);
			if (!c.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class)))
				throw new LangValidationException(f.getLocation(), "Blob expected");
			if (c.isPure()) {
				Blob b = (Blob) c.invoke();
				Blob sizeOfB = new Blob(BigInteger.valueOf(b.getData().length));
				return new ValidatedValCall(InstanceOfConstraint.of(Blob.class)) {
					@Override
					public Val invoke0() {
						return sizeOfB;
					}
				};
			}
			
			return new ValidatedValCall(InstanceOfConstraint.of(Blob.class)) {
				@Override
				public Val invoke0() {
					Blob b = (Blob) c.invoke();
					return new Blob(BigInteger.valueOf(b.getData().length));
				}
			};
		}
		
		@Override
		public String toString() {
			return "byteSize";
		}
	};
	
	public static final Val sub = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (f.getBracketsType() != BracketsType.ROUND) return super.invocation(caller, f);
			Expr[] args = Interpreter.args(f);
			if (args.length != 3)
				throw new LangValidationException(f.getLocation(), "3 arguments expected");
			
			ValidatedValCall argBlob = Interpreter.evaluateValCall(new Scope(caller), args[0]);
			ValidatedValCall argStart = Interpreter.evaluateValCall(new Scope(caller), args[1]);
			ValidatedValCall argEnd = Interpreter.evaluateValCall(new Scope(caller), args[2]);
			if (!argBlob.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class))) {
				throw new LangValidationException(args[0].getLocation(), "Blob expected");
			}
			if (!argStart.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class))) {
				throw new LangValidationException(args[1].getLocation(), "Blob expected");
			}
			if (!argEnd.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class))) {
				throw new LangValidationException(args[2].getLocation(), "Blob expected");
			}
			
			if (argBlob.isPure()) {
				Blob b = (Blob) argBlob.invoke();
				int size = b.getData().length;
				if (!argEnd.getConstraint().implies(caller,
						ComparisonConstraint.of(Blob.ofInt(size + 1/*OR equal*/), ComparisonConstraint.Op.SMALLER)))
					throw new LangValidationException(args[2].getLocation(), "_EndPos must be smaller or equal to blob size (" + size + ")");
				return new ValidatedValCall(InstanceOfConstraint.of(Blob.class)) {
					@Override
					protected Val invoke0() {
						return new Blob(Arrays.copyOfRange(b.getData(), ((Blob)argStart.invoke()).toInt(), ((Blob)argEnd.invoke()).toInt()));
					}
				};
			} else {
				// todo: make it working with unpure
				throw new LangValidationException(args[0].getLocation(), "Blob is not pure");
			}
		}
		
		@Override
		public String toString() {
			return "sub";
		}
	};
	
	
	
	
	/* math / numeric operations */
	
	private static final class BigIntOpVal extends Val {
		private final BiFunction<BigInteger, BigInteger, BigInteger> operation;
		private final BigInteger initial;
		
		private BigIntOpVal(BiFunction<BigInteger, BigInteger, BigInteger> operation, BigInteger initial) {
			this.operation = operation;
			this.initial = initial;
		}
		
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (f.getBracketsType() != BracketsType.ROUND) return super.invocation(caller, f);
			Expr[] args = Interpreter.args(f);
			
			if (args.length < 2)
				throw new LangValidationException(f.getLocation(), "2 or more args expected");
			
			BigInteger sumOfKnownValues = initial;
			List<ValidatedValCall> leftToEvaluate = new ArrayList<>();
			
			for (int i = 0, Len = args.length; i < Len; i++) {
				ValidatedValCall c = Interpreter.evaluateValCall(new Scope(caller), args[i]);
				if (!c.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class)))
					throw new LangValidationException(f.getLocation(), "Number expected (" + i + ")");
				if (c.isPure()) {
					sumOfKnownValues = operation.apply(sumOfKnownValues, ((Blob)c.invoke()).bigInteger());
				} else {
					leftToEvaluate.add(c);
				}
			}
			final BigInteger sokv = sumOfKnownValues;
			if (leftToEvaluate.isEmpty()) return ValidatedValCall.promiseVal(new Blob(sokv));
			return new ValidatedValCall(InstanceOfConstraint.of(Blob.class)) {
				@Override
				public Val invoke0() {
					BigInteger sum = sokv;
					for (ValidatedValCall arg : leftToEvaluate) sum = operation.apply(sum, ((Blob) arg.invoke()).bigInteger());
					return new Blob(sum.toByteArray());
				}
			};
		}
	}
	
	public static final Val sum = new BigIntOpVal(BigInteger::add, BigInteger.ZERO);
	
	public static final Val and = new BigIntOpVal(BigInteger::and, BigInteger.valueOf(-1L));
	
	public static final Val or = new BigIntOpVal(BigInteger::or, BigInteger.ZERO);
	
	public static final Val xor = new BigIntOpVal(BigInteger::xor, BigInteger.ZERO);
	
	
	
	public static final Scope scope = new Scope(null)
			.push("sum", sum)
			.push("and", and)
			.push("or", or)
			.push("xor", xor)
			
			.push("byteSize", byteSize)
			.push("sub", sub)
			
			.push(":", define)
			.push("echo", echo);
			;
			
}
