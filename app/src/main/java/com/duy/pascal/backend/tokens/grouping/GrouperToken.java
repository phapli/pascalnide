package com.duy.pascal.backend.tokens.grouping;


import android.util.Log;

import com.duy.pascal.backend.exceptions.BadOperationTypeException;
import com.duy.pascal.backend.exceptions.ExpectedAnotherTokenException;
import com.duy.pascal.backend.exceptions.ExpectedTokenException;
import com.duy.pascal.backend.exceptions.MissingCommaTokenException;
import com.duy.pascal.backend.exceptions.MissingSemicolonTokenException;
import com.duy.pascal.backend.exceptions.MultipleDefaultValuesException;
import com.duy.pascal.backend.exceptions.NonConstantExpressionException;
import com.duy.pascal.backend.exceptions.NonIntegerException;
import com.duy.pascal.backend.exceptions.NonIntegerIndexException;
import com.duy.pascal.backend.exceptions.NotAStatementException;
import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.exceptions.SameNameException;
import com.duy.pascal.backend.exceptions.UnAssignableTypeException;
import com.duy.pascal.backend.exceptions.UnConvertibleTypeException;
import com.duy.pascal.backend.exceptions.UnrecognizedTokenException;
import com.duy.pascal.backend.exceptions.grouping.GroupingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.ArrayType;
import com.duy.pascal.backend.pascaltypes.BasicType;
import com.duy.pascal.backend.pascaltypes.DeclaredType;
import com.duy.pascal.backend.pascaltypes.PointerType;
import com.duy.pascal.backend.pascaltypes.RecordType;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.duy.pascal.backend.pascaltypes.rangetype.SubrangeType;
import com.duy.pascal.backend.tokens.CommentToken;
import com.duy.pascal.backend.tokens.EOFToken;
import com.duy.pascal.backend.tokens.GroupingExceptionToken;
import com.duy.pascal.backend.tokens.OperatorToken;
import com.duy.pascal.backend.tokens.OperatorTypes;
import com.duy.pascal.backend.tokens.Token;
import com.duy.pascal.backend.tokens.WordToken;
import com.duy.pascal.backend.tokens.basic.ArrayToken;
import com.duy.pascal.backend.tokens.basic.AssignmentToken;
import com.duy.pascal.backend.tokens.basic.BreakToken;
import com.duy.pascal.backend.tokens.basic.ColonToken;
import com.duy.pascal.backend.tokens.basic.CommaToken;
import com.duy.pascal.backend.tokens.basic.DoToken;
import com.duy.pascal.backend.tokens.basic.DowntoToken;
import com.duy.pascal.backend.tokens.basic.ElseToken;
import com.duy.pascal.backend.tokens.basic.ExitToken;
import com.duy.pascal.backend.tokens.basic.ForToken;
import com.duy.pascal.backend.tokens.basic.IfToken;
import com.duy.pascal.backend.tokens.basic.OfToken;
import com.duy.pascal.backend.tokens.basic.PeriodToken;
import com.duy.pascal.backend.tokens.basic.RepeatToken;
import com.duy.pascal.backend.tokens.basic.SemicolonToken;
import com.duy.pascal.backend.tokens.basic.ThenToken;
import com.duy.pascal.backend.tokens.basic.ToToken;
import com.duy.pascal.backend.tokens.basic.UntilToken;
import com.duy.pascal.backend.tokens.basic.WhileToken;
import com.duy.pascal.backend.tokens.value.ValueToken;
import com.js.interpreter.ast.VariableDeclaration;
import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.instructions.Assignment;
import com.js.interpreter.ast.instructions.BreakInstruction;
import com.js.interpreter.ast.instructions.Executable;
import com.js.interpreter.ast.instructions.ExitInstruction;
import com.js.interpreter.ast.instructions.InstructionGrouper;
import com.js.interpreter.ast.instructions.NoneInstruction;
import com.js.interpreter.ast.instructions.case_statement.CaseInstruction;
import com.js.interpreter.ast.instructions.conditional.DowntoForStatement;
import com.js.interpreter.ast.instructions.conditional.ForStatement;
import com.js.interpreter.ast.instructions.conditional.IfStatement;
import com.js.interpreter.ast.instructions.conditional.RepeatInstruction;
import com.js.interpreter.ast.instructions.conditional.WhileStatement;
import com.js.interpreter.ast.returnsvalue.ConstantAccess;
import com.js.interpreter.ast.returnsvalue.FieldAccess;
import com.js.interpreter.ast.returnsvalue.FunctionCall;
import com.js.interpreter.ast.returnsvalue.LValue;
import com.js.interpreter.ast.returnsvalue.RValue;
import com.js.interpreter.ast.returnsvalue.UnaryOperatorEvaluation;
import com.js.interpreter.ast.returnsvalue.operators.BinaryOperatorEvaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class GrouperToken extends Token {
    private static final String TAG = GrouperToken.class.getSimpleName();
    LinkedBlockingQueue<Token> queue;
    Token next = null;

    public GrouperToken(LineInfo line) {
        super(line);
        queue = new LinkedBlockingQueue<Token>();
    }

    private Token get_next() throws GroupingException {
        if (next == null) {
            while (true) {
                try {
                    next = queue.take();
                } catch (InterruptedException e) {
                    continue;
                }
                break;
            }
        }
        exceptioncheck(next);
        if (next instanceof CommentToken) {
            next = null;
            return get_next();
        }
        return next;
    }

    public boolean hasNext() throws GroupingException {
        return !(get_next() instanceof EOFToken);
    }

    private void exceptioncheck(Token t) throws GroupingException {
        if (t instanceof GroupingExceptionToken) {
            throw ((GroupingExceptionToken) t).exception;
        }
    }

    public void put(Token t) {
        while (true) {
            try {
                queue.put(t);
            } catch (InterruptedException e) {
                continue;
            }
            break;
        }
    }

    public abstract String toCode();

    public Token take() throws ExpectedAnotherTokenException, GroupingException {
        Token result = get_next();

        if (result instanceof EOFToken) {
            throw new ExpectedAnotherTokenException(result.lineInfo);
        }
        while (true) {
            try {
                next = queue.take();
                exceptioncheck(next);

                return result;
            } catch (InterruptedException e) {
            }
        }
    }

    public Token peek() throws GroupingException {
        return get_next();
    }

    public Token peek_no_EOF() throws ExpectedAnotherTokenException,
            GroupingException {
        Token result = peek();
        if (result instanceof EOFToken) {
            throw new ExpectedAnotherTokenException(result.lineInfo);
        }
        return result;
    }

    @Override
    public String toString() {
        try {
            return get_next().toString() + ',' + queue.toString();
        } catch (GroupingException e) {
            return "Exception: " + e.toString();
        }
    }

    public String next_word_value() throws ParsingException {
        return take().get_word_value().name;
    }

    public void assert_next_semicolon() throws ParsingException {
        Token t = take();
        if (!(t instanceof SemicolonToken)) {
            throw new MissingSemicolonTokenException(t);
        }
    }

    public void assertNextComma() throws ParsingException {
        Token t = take();
        if (!(t instanceof CommaToken)) {
            throw new MissingCommaTokenException(t);
        }
    }

    public DeclaredType get_next_pascal_type(ExpressionContext context)
            throws ParsingException {
        Token n = take();
        if (n instanceof ArrayToken) {
            return getArrayType(context);
        }
        if (n instanceof RecordToken) {
//            throw new UnSupportTokenException(n.lineInfo, n);
            RecordToken r = (RecordToken) n;
            RecordType result = new RecordType();
            result.variable_types = r.get_variable_declarations(context);
            return result;
        }
        if (n instanceof OperatorToken && ((OperatorToken) n).type == OperatorTypes.DEREF) {
            DeclaredType pointed_type = get_next_pascal_type(context);
            return new PointerType(pointed_type);
        }
        /*if (n instanceof ClassToken) {
            ClassToken o = (ClassToken)n;
			ClassType result = new ClassType();
			throw new ExpectedTokenException("[asdf]", n);
		}*/
        if (!(n instanceof WordToken)) {
            throw new ExpectedTokenException("[Type Identifier]", n);
        }
        return ((WordToken) n).to_basic_type(context);
    }

    private DeclaredType getArrayType(ExpressionContext context)
            throws ParsingException {
        Token n = peek_no_EOF();
        if (n instanceof BracketedToken) {
            BracketedToken bracket = (BracketedToken) take();
            return getArrayType(bracket, context);
        } else if (n instanceof OfToken) {
            take();
            DeclaredType elementType = get_next_pascal_type(context);
            return new ArrayType<DeclaredType>(elementType, new SubrangeType());
        } else {
            throw new ExpectedTokenException("of", n);
        }
    }

    private DeclaredType getArrayType(BracketedToken bounds, ExpressionContext context)
            throws ParsingException {
        SubrangeType bound = new SubrangeType(bounds, context);
        DeclaredType elementType;
        if (bounds.hasNext()) {
            Token t = bounds.take();
            if (!(t instanceof CommaToken)) {
                throw new ExpectedTokenException("']' or ','", t);
            }
            elementType = getArrayType(bounds, context);
        } else {
            Token next = take();
            if (!(next instanceof OfToken)) {
                throw new ExpectedTokenException("of", next);
            }
            elementType = get_next_pascal_type(context);
        }
        Log.d(TAG, "getArrayType: " + elementType.toString());
        ArrayType arrayType;
        return new ArrayType<>(elementType, bound);
    }

    public RValue getNextExpression(ExpressionContext context,
                                    precedence precedence, Token next) throws ParsingException {

        RValue nextTerm;
        if (next instanceof OperatorToken) {
            OperatorToken nextOperator = (OperatorToken) next;
            if (!nextOperator.can_be_unary() || nextOperator.postfix()) {
                throw new BadOperationTypeException(next.lineInfo,
                        nextOperator.type);
            }
            nextTerm = UnaryOperatorEvaluation.generateOp(context, getNextExpression(context, nextOperator.type.getPrecedence()), nextOperator.type, nextOperator.lineInfo);
        } else {
            nextTerm = getNextTerm(context, next);
        }

        while ((next = peek()).getOperatorPrecedence() != null) {
            if (next instanceof OperatorToken) {
                OperatorToken nextOperator = (OperatorToken) next;
                if (nextOperator.type.getPrecedence().compareTo(precedence) >= 0) {
                    break;
                }
                take();
                if (nextOperator.postfix()) {
                    return UnaryOperatorEvaluation.generateOp(context, nextTerm, nextOperator.type, nextOperator.lineInfo);
                }
                RValue nextvalue = getNextExpression(context,
                        nextOperator.type.getPrecedence());
                OperatorTypes operationtype = ((OperatorToken) next).type;
                DeclaredType type1 = nextTerm.get_type(context).declType;
                DeclaredType type2 = nextvalue.get_type(context).declType;
                try {
                    operationtype.verifyBinaryOperation(type1, type2);
                } catch (BadOperationTypeException e) {
                    throw new BadOperationTypeException(next.lineInfo, type1,
                            type2, nextTerm, nextvalue, operationtype);
                }
                nextTerm = BinaryOperatorEvaluation.generateOp(context,
                        nextTerm, nextvalue, operationtype,
                        nextOperator.lineInfo);
            } else if (next instanceof PeriodToken) {
                take();
                next = take();
                if (!(next instanceof WordToken)) {
                    throw new ExpectedTokenException("[Element Name]", next);
                }
                nextTerm = new FieldAccess(nextTerm, (WordToken) next);
            } else if (next instanceof BracketedToken) {
                take(); //comma token
                BracketedToken b = (BracketedToken) next;

                RuntimeType mRuntimeType = nextTerm.get_type(context);
                RValue mUnConverted = b.getNextExpression(context);
                RValue mConverted = BasicType.Integer.convert(mUnConverted, context);

                if (mConverted == null) {
                    throw new NonIntegerIndexException(mUnConverted);
                }

                nextTerm = mRuntimeType.declType.generateArrayAccess(nextTerm, mConverted);

                while (b.hasNext()) {
                    next = b.take();
                    if (!(next instanceof CommaToken)) {
                        throw new ExpectedTokenException("]", next);
                    }
                    RuntimeType type = nextTerm.get_type(context);
                    RValue unconvert = b.getNextExpression(context);
                    RValue convert = BasicType.Integer.convert(unconvert, context);

                    if (convert == null) {
                        throw new NonIntegerIndexException(unconvert);
                    }
                    nextTerm = type.declType.generateArrayAccess(nextTerm, convert);
                }


            }
        }
        return nextTerm;
    }

    public RValue getNextExpression(ExpressionContext context,
                                    precedence precedence) throws ParsingException {
        return getNextExpression(context, precedence, take());
    }

    public RValue getNextTerm(ExpressionContext context, Token next)
            throws ParsingException {
        if (next instanceof ParenthesizedToken) {
            return ((ParenthesizedToken) next).get_single_value(context);
        } else if (next instanceof ValueToken) {
            return new ConstantAccess(((ValueToken) next).getValue(),
                    next.lineInfo);
        } else if (next instanceof WordToken) {
            WordToken name = ((WordToken) next);
            next = peek();

            if (next instanceof ParenthesizedToken) {
                List<RValue> arguments;
                if (name.name.equalsIgnoreCase("writeln") || name.name.equalsIgnoreCase("write")) {
                    arguments = ((ParenthesizedToken) take()).getArgumentsForOutput(context);
                } else {
                    arguments = ((ParenthesizedToken) take()).get_arguments_for_call(context);
                }
                return FunctionCall.generateFunctionCall(name, arguments, context);
            } else {
                return context.getIdentifierValue(name);
            }

        }/* else if (next instanceof CommentToken) {
            return getNextTerm(context);
        } */ else {
            throw new UnrecognizedTokenException(next);
        }
    }

    public RValue getNextTerm(ExpressionContext context)
            throws ParsingException {
        return getNextTerm(context, take());
    }

    public RValue getNextExpression(ExpressionContext context)
            throws ParsingException {
        return getNextExpression(context, precedence.NoPrecedence);
    }

    public RValue getNextExpression(ExpressionContext context, Token first)
            throws ParsingException {
        return getNextExpression(context, precedence.NoPrecedence, first);
    }

    public List<VariableDeclaration> get_variable_declarations(
            ExpressionContext context) throws ParsingException {
        List<VariableDeclaration> result = new ArrayList<>();
        /*
         * reusing it, so it is further out of scope than necessary
		 */
        List<WordToken> names = new ArrayList<>();
        Token next;
        do {
            do {
                next = take();
                if (!(next instanceof WordToken)) {
                    throw new ExpectedTokenException("[Variable Identifier]", next);
                }
                names.add((WordToken) next);
                next = take();
            } while (next instanceof CommaToken);
            if (!(next instanceof ColonToken)) {
                throw new ExpectedTokenException(":", next);
            }
            DeclaredType type = get_next_pascal_type(context);

            //process string with define length
            if (type.equals(BasicType.StringBuilder)) {
                if (peek() instanceof BracketedToken) {
                    BracketedToken bracketedToken = (BracketedToken) take();

                    RValue unconverted = bracketedToken.getNextExpression(context);
                    RValue converted = BasicType.Integer.convert(unconverted, context);

                    if (converted == null) {
                        throw new NonIntegerException(unconverted);
                    }

                    if (bracketedToken.hasNext()) {
                        throw new ExpectedTokenException("]", bracketedToken.take());
                    }
                    ((BasicType) type).setLength(converted);
                }
            }


            Object defaultValue = null;
            if (peek() instanceof OperatorToken) {
                if (((OperatorToken) peek()).type == OperatorTypes.EQUALS) {
                    take();
                    //set default value for array
                    if (type instanceof ArrayType) {
                        DeclaredType elementTypeOfArray = ((ArrayType) type).element_type;
                        ParenthesizedToken bracketedToken = (ParenthesizedToken) take();
                        int size = ((ArrayType) type).getBounds().size;
                        Object[] objects = new Object[size];
                        for (int i = 0; i < size; i++) {
                            if (!bracketedToken.hasNext()) {
                                // TODO: 27-Apr-17  exception
                            }
                            objects[i] = getDefaultValueArray(context, bracketedToken, elementTypeOfArray);
                        }
                        Log.d(TAG, "getDefaultValueArray: " + Arrays.toString(objects));
                        defaultValue = objects;
                    } else { //set default single value
                        RValue unConvert = getNextExpression(context);
                        RValue converted = type.convert(unConvert, context);
                        if (converted == null) {
                            throw new UnConvertibleTypeException(unConvert,
                                    unConvert.get_type(context).declType, type,
                                    true);
                        }
                        defaultValue = converted.compileTimeValue(context);
                        if (defaultValue == null) {
                            throw new NonConstantExpressionException(converted);
                        }
                        if (names.size() != 1) {
                            throw new MultipleDefaultValuesException(
                                    converted.getLineNumber());
                        }
                    }
                }
            }

            assert_next_semicolon();
            for (WordToken s : names) {
                VariableDeclaration v = new VariableDeclaration(s.name, type,
                        defaultValue, s.lineInfo);
                verifyNonConflictingSymbol(result, v);
                result.add(v);
            }
            names.clear(); // reusing the list object
            next = peek();
        } while (next instanceof WordToken);
        return result;
    }

    public Object getDefaultValueArray(ExpressionContext context,
                                       ParenthesizedToken parenthesizedToken,
                                       DeclaredType elementTypeOfArray) throws ParsingException {
        if (parenthesizedToken.hasNext()) {
            if (elementTypeOfArray instanceof ArrayType) {
                if (parenthesizedToken.peek() instanceof ParenthesizedToken) {
                    ParenthesizedToken child = (ParenthesizedToken) parenthesizedToken.take();
                    Object[] objects = new Object[((ArrayType) elementTypeOfArray).getBounds().size];
                    for (int i = 0; i < objects.length; i++) {
                        objects[i] = getDefaultValueArray(context, child, ((ArrayType) elementTypeOfArray).element_type);
                    }
                    if (child.hasNext()) {
                        // TODO: 27-Apr-17  exception
                    }
                    if (parenthesizedToken.hasNext()) {
                        parenthesizedToken.assertNextComma();
                    }
                    return objects;
                } else {
                    // TODO: 27-Apr-17 throw exception
                }
            } else {
                RValue unconvert = parenthesizedToken.getNextExpression(context);
                RValue converted = elementTypeOfArray.convert(unconvert, context);
                if (context == null) {
                    // TODO: 27-Apr-17  throw exception
                }
                if (parenthesizedToken.hasNext()) {
                    parenthesizedToken.assertNextComma();
                }
                return converted.compileTimeValue(context);
            }
        } else {
            // TODO: 27-Apr-17  throw exception
        }
        return null;
    }

    private void verifyNonConflictingSymbol(List<VariableDeclaration> result, VariableDeclaration variable) throws SameNameException {
        for (VariableDeclaration variableDeclaration : result) {
            if (variableDeclaration.getName().equalsIgnoreCase(variable.getName())) {
                throw new SameNameException(variableDeclaration, variable);
            }
        }
    }

    public RValue get_single_value(ExpressionContext context)
            throws ParsingException {
        RValue result = getNextExpression(context);
        if (hasNext()) {
            Token next = take();
            throw new ExpectedTokenException(getClosingText(), next);
        }
        return result;
    }

    public Executable get_next_command(ExpressionContext context)
            throws ParsingException {
        Token next = take();
        LineInfo initialline = next.lineInfo;
        if (next instanceof IfToken) {
            RValue condition = getNextExpression(context);
            next = take();
            if (!(next instanceof ThenToken)) {
                throw new ExpectedTokenException("then", next);
            }
            Executable command = get_next_command(context);
            Executable else_command = null;
            next = peek();
            if (next instanceof ElseToken) {
                take();
                else_command = get_next_command(context);
            }
            return new IfStatement(condition, command, else_command,
                    initialline);
        } else if (next instanceof WhileToken) {
            RValue condition = getNextExpression(context);
            next = take();
            assert (next instanceof DoToken);
            Executable command = get_next_command(context);
            return new WhileStatement(condition, command, initialline);
        } else if (next instanceof BeginEndToken) {
            InstructionGrouper begin_end_preprocessed = new InstructionGrouper(
                    initialline);
            BeginEndToken cast_token = (BeginEndToken) next;

            while (cast_token.hasNext()) {
                begin_end_preprocessed.add_command(cast_token
                        .get_next_command(context));
                if (cast_token.hasNext()) {
                    cast_token.assert_next_semicolon();
                }
            }
            return begin_end_preprocessed;
        } else if (next instanceof ForToken) {
            RValue tmp_val = getNextExpression(context);
            LValue tmp_var = tmp_val.asLValue(context);
            if (tmp_var == null) {
                throw new UnAssignableTypeException(tmp_val);
            }
            next = take();
            assert (next instanceof AssignmentToken);
            RValue first_value = getNextExpression(context);
            next = take();
            boolean downto = false;
            if (next instanceof DowntoToken) {
                downto = true;
            } else if (!(next instanceof ToToken)) {
                throw new ExpectedTokenException("[To] or [Downto]", next);
            }
            RValue last_value = getNextExpression(context);
            next = take();
            assert (next instanceof DoToken);
            Executable result;
            if (downto) { // TODO probably should merge these two types
                result = new DowntoForStatement(context, tmp_var, first_value,
                        last_value, get_next_command(context), initialline);
            } else {
                result = new ForStatement(context, tmp_var, first_value,
                        last_value, get_next_command(context), initialline);
            }
            return result;
        } else if (next instanceof RepeatToken) {
            InstructionGrouper command = new InstructionGrouper(initialline);

            while (!(peek_no_EOF() instanceof UntilToken)) {
                command.add_command(get_next_command(context));
                if (!(peek_no_EOF() instanceof UntilToken)) {
                    assert_next_semicolon();
                }
            }
            next = take();
            if (!(next instanceof UntilToken)) {
                throw new ExpectedTokenException("until", next);
            }
            RValue condition = getNextExpression(context);
            return new RepeatInstruction(command, condition, initialline);
        } else if (next instanceof CaseToken) {
            return new CaseInstruction((CaseToken) next, context);
        } else if (next instanceof SemicolonToken) {
            return new NoneInstruction(next.lineInfo);
        } else if (next instanceof BreakToken) {
            return new BreakInstruction(next.lineInfo);
        } else if (next instanceof ExitToken) {
            return new ExitInstruction(next.lineInfo);
        } else {
            try {
                return context.handleUnrecognizedStatement(next, this);
            } catch (ParsingException ignored) {
            }

            RValue r = getNextExpression(context, next);
            next = peek();
            if (next instanceof AssignmentToken) {
                take();
                LValue left = r.asLValue(context);
                if (left == null) {
                    throw new UnAssignableTypeException(r);
                }
                RValue value_to_assign = getNextExpression(context);
                DeclaredType output_type = left.get_type(context).declType;
                DeclaredType input_type = value_to_assign.get_type(context).declType;
                /*
                 * Does not have to be writable to assign value to variable.
				 */
                RValue converted = output_type.convert(value_to_assign,
                        context);
                if (converted == null) {
                    throw new UnConvertibleTypeException(value_to_assign,
                            input_type, output_type, true);
                }
                return new Assignment(left, output_type
                        .cloneValue(converted), next.lineInfo);
            } else if (r instanceof Executable) {
                return (Executable) r;
            } else {
                throw new NotAStatementException(r);
            }

        }
    }

    protected abstract String getClosingText();

}
