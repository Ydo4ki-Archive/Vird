package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.base.*;
import com.ydo4ki.vird.lang.*;
import com.ydo4ki.vird.lang.constraint.*;
import lombok.NonNull;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @since 5/8/2025 11:40 PM
 * @author Sulphuris
 */
public final class Functional {
	
	public static final BracketsType round = new BracketsType('(',')');
	public static final BracketsType square = new BracketsType('[',']');
	public static final BracketsType braces = new BracketsType('{','}');
	/* meta */
	
	
	public static final Val define = new Define();
	
	/* functional */
	
	public static final Val echo = new Val() {
		@Override
		public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
			if (!f.getBracketsType().equals(round)) return super.invocation(caller, f);
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
			ValidatedValCall call = FileInterpreter.evaluateValCall(caller, arg);
			return new ValidatedValCall(new EqualityConstraint(Val.unit)) {
				@Override
				public @NonNull Val invoke0() throws RuntimeOperation {
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
			if (!f.getBracketsType().equals(round)) return super.invocation(caller, f);
			Expr[] args = VirdUtil.args(f);
			
			if (args.length != 1)
				throw new LangValidationException(f.getLocation(), "1 argument expected");
			
			ValidatedValCall c = FileInterpreter.evaluateValCall(new Scope(caller), args[0]);
			if (!c.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class)))
				throw new LangValidationException(f.getLocation(), "Blob expected");
			if (c.isPure()) {
				Blob b = (Blob) ValidatedValCall.invokePure(f.getLocation(), c);
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
				public Val invoke0() throws RuntimeOperation {
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
			if (!f.getBracketsType().equals(round)) return super.invocation(caller, f);
			Expr[] args = VirdUtil.args(f);
			if (args.length != 3)
				throw new LangValidationException(f.getLocation(), "3 arguments expected");
			
			ValidatedValCall argBlob = FileInterpreter.evaluateValCall(new Scope(caller), args[0]);
			ValidatedValCall argStart = FileInterpreter.evaluateValCall(new Scope(caller), args[1]);
			ValidatedValCall argEnd = FileInterpreter.evaluateValCall(new Scope(caller), args[2]);
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
				Blob b = (Blob) ValidatedValCall.invokePure(f.getLocation(), argBlob);
				int size = b.getData().length;
				if (!argEnd.getConstraint().implies(caller,
						ComparisonConstraint.of(Blob.ofInt(size + 1/*OR equal*/), ComparisonConstraint.Op.SMALLER)))
					throw new LangValidationException(args[2].getLocation(), "_EndPos must be smaller or equal to blob size (" + size + ")");
				return new ValidatedValCall(InstanceOfConstraint.of(Blob.class)) {
					@Override
					protected Val invoke0() throws RuntimeOperation {
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
	
	public static final Val sum = new BigIntOpVal(BigInteger::add, BigInteger.ZERO);
	
	public static final Val and = new BigIntOpVal(BigInteger::and, BigInteger.valueOf(-1L));
	
	public static final Val or = new BigIntOpVal(BigInteger::or, BigInteger.ZERO);
	
	public static final Val xor = new BigIntOpVal(BigInteger::xor, BigInteger.ZERO);
	
	public static final Val _do = new Do();
	
	public static final Struct declaration = new Struct(
			InstanceOfConstraint.of(Symbol.class),
			FreeConstraint.INSTANCE
	);
	
	public static final Scope scope = new Scope(null)
			.push("sum", sum)
			.push("and", and)
			.push("or", or)
			.push("xor", xor)
			
			.push("byteSize", byteSize)
			.push("sub", sub)
			
			.push("do", _do)
			.push(":", define)
			.push("echo", echo);
	
	public static ValidatedValCall newDeclaration(Symbol sym, Val val) {
		try {
			return declaration.newVal(null, null, sym, val);
		} catch (LangValidationException e) {
			throw new AssertionError(e);
		}
	}
			
}
