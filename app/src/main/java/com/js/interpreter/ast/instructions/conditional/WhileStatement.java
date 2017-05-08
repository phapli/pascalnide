package com.js.interpreter.ast.instructions.conditional;

import com.duy.pascal.backend.debugable.DebuggableExecutable;
import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.frontend.debug.DebugManager;
import com.js.interpreter.ast.expressioncontext.CompileTimeContext;
import com.js.interpreter.ast.instructions.Executable;
import com.js.interpreter.ast.instructions.ExecutionResult;
import com.js.interpreter.ast.instructions.NoneInstruction;
import com.js.interpreter.ast.returnsvalue.ConstantAccess;
import com.js.interpreter.ast.returnsvalue.ReturnValue;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;

public class WhileStatement extends DebuggableExecutable {
    ReturnValue condition;

    Executable command;
    LineInfo line;

    public WhileStatement(ReturnValue condition, Executable command,
                          LineInfo line) {
        this.condition = condition;
        this.command = command;
        this.line = line;
    }

    @Override
    public ExecutionResult executeImpl(VariableContext f,
                                       RuntimeExecutable<?> main) throws RuntimePascalException {
        while_loop:
        while ((Boolean) condition.getValue(f, main)) {
            DebugManager.outputConditionWhile(main.getDebugListener(), true);
            switch (command.execute(f, main)) {
                case BREAK:
                    break while_loop;
                case EXIT:
                    return ExecutionResult.EXIT;
            }
        }
//        DebugManager.outputConditionWhile(main.getDebugListener(), false);

        return ExecutionResult.NONE;
    }

    @Override
    public String toString() {
        return "while [" + condition + "] do [" + command + ']';
    }

    @Override
    public LineInfo getLineNumber() {
        return line;
    }

    @Override
    public Executable compileTimeConstantTransform(CompileTimeContext c)
            throws ParsingException {
        Executable comm = command.compileTimeConstantTransform(c);
        Object cond = condition.compileTimeValue(c);
        if (cond != null) {
            if (!((Boolean) cond)) {
                return new NoneInstruction(line);
            } else {
                return new WhileStatement(new ConstantAccess(true,
                        condition.getLineNumber()), comm, line);
            }
        }
        return new WhileStatement(condition, comm, line);
    }
}
