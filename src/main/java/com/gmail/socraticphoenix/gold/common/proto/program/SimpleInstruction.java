/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 socraticphoenix@gmail.com
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.gold.common.proto.program;

import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.inversey.many.Consumer3;

import java.util.List;
import java.util.Map;

public class SimpleInstruction<T extends Memory> extends AbstractInstruction<T> {
    private Consumer3<Map<String, Value>, T, ProgramContext<T>> exec;

    public SimpleInstruction(String name, String id, String help, String doc, String op, boolean gensId, List<Argument<T>> arguments, Consumer3<Map<String, Value>, T, ProgramContext<T>> exec) {
        super(arguments, name, id, help, doc, op, gensId);
        this.exec = exec;
    }

    @Override
    public void exec(Map<String, Value> arguments, T memory, ProgramContext<T> context) {
        this.exec.call(arguments, memory, context);
    }

}
