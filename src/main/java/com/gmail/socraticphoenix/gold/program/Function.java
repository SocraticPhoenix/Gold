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
package com.gmail.socraticphoenix.gold.program;

import com.gmail.socraticphoenix.gold.ast.Loc;
import com.gmail.socraticphoenix.gold.ast.Node;
import com.gmail.socraticphoenix.gold.ast.SequenceNode;
import com.gmail.socraticphoenix.gold.program.memory.Memory;

import java.util.List;

public class Function<T extends Memory> {
    private Loc loc;
    private String name;
    private SequenceNode<T> content;

    public Function(Loc loc, String name, SequenceNode<T> content) {
        this.content = content;
        this.loc = loc;
        this.name = name;
    }

    public void exec(T memory, ProgramContext<T> context) {
        context.check();
        try {
            this.content.exec(memory, context);
        } catch (Throwable e) {
            throw new GoldExecutionError(context.error(this.loc, "function", this.name), e);
        }
    }

}
