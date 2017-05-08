package com.duy.pascal.backend.pascaltypes.typeconversion;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.BasicType;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.js.interpreter.ast.expressioncontext.CompileTimeContext;
import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.returnsvalue.LeftValue;
import com.js.interpreter.ast.returnsvalue.ReturnValue;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;

public class NumberToLongType implements ReturnValue {
    private ReturnValue[] outputFormat;
    private ReturnValue other;


    public NumberToLongType(ReturnValue other) {
        this.other = other;
        this.outputFormat = other.getOutputFormat();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public ReturnValue[] getOutputFormat() {
        return outputFormat;
    }

    @Override
    public void setOutputFormat(ReturnValue[] formatInfo) {
        this.outputFormat = formatInfo;
    }


    @Override
    public Object getValue(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException {
        Number i = (Number) other.getValue(f, main);
        return i.longValue();
    }

    @Override
    public RuntimeType getType(ExpressionContext f)
            throws ParsingException {
        return new RuntimeType(BasicType.Long, false);
    }

    @Override
    public LineInfo getLineNumber() {
        return other.getLineNumber();
    }

    @Override
    public Object compileTimeValue(CompileTimeContext context)
            throws ParsingException {
        Object o = other.compileTimeValue(context);
        if (o != null) {
            return ((Number) o).longValue();
        } else {
            return null;
        }
    }


    @Override
    public ReturnValue compileTimeExpressionFold(CompileTimeContext context)
            throws ParsingException {
        return new NumberToLongType(other.compileTimeExpressionFold(context));
    }

    @Override
    public LeftValue asLValue(ExpressionContext f) {
        return null;
    }
}
