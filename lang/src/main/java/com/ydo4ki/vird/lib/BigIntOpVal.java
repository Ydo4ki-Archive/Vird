package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.FileInterpreter;
import com.ydo4ki.vird.VirdUtil;
import com.ydo4ki.vird.base.Expr;
import com.ydo4ki.vird.base.ExprList;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Blob;
import com.ydo4ki.vird.lang.LangValidationException;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @since 6/3/2025 12:08 AM
 */
final class BigIntOpVal extends Val {
	private final BiFunction<BigInteger, BigInteger, BigInteger> operation;
	private final BigInteger initial;
	
	BigIntOpVal(BiFunction<BigInteger, BigInteger, BigInteger> operation, BigInteger initial) {
		this.operation = operation;
		this.initial = initial;
	}
	
	@Override
	public ValidatedValCall invocation(Scope caller, ExprList f) throws LangValidationException {
		if (!f.getBracketsType().equals(Functional.round)) return super.invocation(caller, f);
		Expr[] args = VirdUtil.args(f);
		
		if (args.length < 2)
			throw new LangValidationException(f.getLocation(), "2 or more args expected");
		
		BigInteger sumOfKnownValues = initial;
		List<ValidatedValCall> leftToEvaluate = new ArrayList<>();
		
		for (int i = 0, Len = args.length; i < Len; i++) {
			ValidatedValCall c = FileInterpreter.evaluateValCall(new Scope(caller), args[i]);
			if (!c.getConstraint().implies(caller, InstanceOfConstraint.of(Blob.class)))
				throw new LangValidationException(f.getLocation(), "Number expected (" + i + ")");
			if (c.isPure()) {
				sumOfKnownValues = operation.apply(sumOfKnownValues, ((Blob) c.invoke()).bigInteger());
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
				for (ValidatedValCall arg : leftToEvaluate)
					sum = operation.apply(sum, ((Blob) arg.invoke()).bigInteger());
				return new Blob(sum.toByteArray());
			}
		};
	}
}
