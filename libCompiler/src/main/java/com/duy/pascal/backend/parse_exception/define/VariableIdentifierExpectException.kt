/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.backend.parse_exception.define

import com.duy.pascal.backend.ast.expressioncontext.ExpressionContext
import com.duy.pascal.backend.linenumber.LineInfo
import com.duy.pascal.backend.parse_exception.ParsingException

/**
 * Created by Duy on 30-May-17.
 */

class VariableIdentifierExpectException(line: LineInfo, var name: String,
                                        var scope: ExpressionContext) : ParsingException(line) {

    override val message: String?
        get() = "Variable identifier expected: name";
}
