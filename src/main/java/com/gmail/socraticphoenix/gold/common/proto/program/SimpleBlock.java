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

import com.gmail.socraticphoenix.gold.ast.BlockNode;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.inversey.many.Consumer3;

import java.util.List;

public class SimpleBlock<T extends Memory> extends AbstractBlock<T> {
    private Consumer3<BlockNode<T>, T, ProgramContext<T>> exec;

    public SimpleBlock(List<String> tags, String name, String help, String doc, String op, Consumer3<BlockNode<T>, T, ProgramContext<T>> exec) {
        super(tags, name, help, doc, op);
        this.exec = exec;
    }

    @Override
    public void exec(BlockNode<T> node, T memory, ProgramContext<T> context) {
        this.exec.call(node, memory, context);
    }

}
