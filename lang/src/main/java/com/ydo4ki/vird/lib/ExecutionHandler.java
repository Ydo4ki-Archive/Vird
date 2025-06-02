package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Scope;
import com.ydo4ki.vird.lang.ValidatedValCall;

public interface ExecutionHandler {
	void handleCall(Scope caller, ValidatedValCall call);
	void handleVal(Scope caller, Val val);
}
