package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.Symbol;
import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.constraint.Constraint;
import com.ydo4ki.vird.lang.constraint.InstanceOfConstraint;

/**
 * @since 6/3/2025 12:11 AM
 * @author Sulphuris
 */
@Deprecated
public class Declaration extends Val {
	private final Symbol name;
	private final Val val;
	
	public Declaration(Symbol name, Val val) {
		this.name = name;
		this.val = val;
	}
	
	public static class DeclarationConstraint extends InstanceOfConstraint {
		private final Symbol name;
		private final ValidatedValCall call;
		
		public DeclarationConstraint(Symbol name, ValidatedValCall call) {
			super(Declaration.class);
			this.name = name;
			this.call = call;
		}
		
		@Override
		public boolean test(Scope scope, Val value) {
			return super.test(scope, value)
					&& ((Declaration)value).name.equals(this.name)
					&& call.getConstraint().test(scope, ((Declaration) value).val);
		}
		
		@Override
		public boolean implies(Scope scope, Constraint other) {
			if (other instanceof DeclarationConstraint) {
				return name.equals(((DeclarationConstraint) other).name)
						&& call.getConstraint().implies(scope, ((DeclarationConstraint) other).call.getConstraint());
			}
			return super.implies(scope, other);
		}
	}
}
