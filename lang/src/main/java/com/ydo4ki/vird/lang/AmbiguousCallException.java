package com.ydo4ki.vird.lang;

import com.ydo4ki.vird.base.LangException;
import com.ydo4ki.vird.base.Location;

public class AmbiguousCallException extends LangException {
    
    public AmbiguousCallException(Location location, String message) {
        super(location, message);
    }
    
    public AmbiguousCallException(Location location, String message, Throwable cause) {
        super(location, message, cause);
    }
}
