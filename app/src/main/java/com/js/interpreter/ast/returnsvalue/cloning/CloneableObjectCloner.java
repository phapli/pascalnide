package com.js.interpreter.ast.returnsvalue.cloning;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.js.interpreter.ast.expressioncontext.CompileTimeContext;
import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.returnsvalue.LeftValue;
import com.js.interpreter.ast.returnsvalue.ReturnValue;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;
import com.js.interpreter.runtime.variables.ContainsVariables;

public class CloneableObjectCloner implements ReturnValue {
    protected ReturnValue[] outputFormat;
    private ReturnValue r;

    public CloneableObjectCloner(ReturnValue r) {
        this.r = r;
        this.outputFormat = r.getOutputFormat();
    }

    @Override
    public RuntimeType getType(ExpressionContext f)
            throws ParsingException {
        return r.getType(f);
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
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Object getValue(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException {
        ContainsVariables c = (ContainsVariables) r.getValue(f, main);
        return c.clone();
    }

    @Override
    public LineInfo getLineNumber() {
        return r.getLineNumber();
    }

    @Override
    public Object compileTimeValue(CompileTimeContext context)
            throws ParsingException {
        ContainsVariables c = (ContainsVariables) r
                .compileTimeValue(context);
        return c.clone();
    }


    @Override
    public ReturnValue compileTimeExpressionFold(CompileTimeContext context)
            throws ParsingException {
        return new CloneableObjectCloner(r.compileTimeExpressionFold(context));
    }

    @Override
    public LeftValue asLValue(ExpressionContext f) {
        return null;
    }
}