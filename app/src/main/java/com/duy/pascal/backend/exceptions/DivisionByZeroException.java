package com.duy.pascal.backend.exceptions;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.js.interpreter.runtime.exception.internal.InternalInterpreterException;

/**
 * Created by Duy on 01-Mar-17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DivisionByZeroException extends InternalInterpreterException {
    public DivisionByZeroException(LineInfo line) {
        super(line);
    }

    @Override
    public String getInternalError() {
        return "Division by zero";
    }

    @Override
    public String getMessage() {
        return "Internal Interpreter Error: " + getInternalError();
    }
}
