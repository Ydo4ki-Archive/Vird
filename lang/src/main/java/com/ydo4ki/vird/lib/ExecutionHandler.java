package com.ydo4ki.vird.lib;

import com.ydo4ki.vird.base.Val;
import com.ydo4ki.vird.lang.Env;
import com.ydo4ki.vird.lang.ValidatedValCall;

public interface ExecutionHandler {
	void handleCall(Env caller, ValidatedValCall call);
	void handleVal(Env caller, Val val);
}
