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
package com.gmail.socraticphoenix.gold.ast;

import com.gmail.socraticphoenix.gold.program.Block;
import com.gmail.socraticphoenix.gold.program.GoldExecutionError;
import com.gmail.socraticphoenix.gold.program.GoldStateException;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.memory.Memory;

import java.util.List;
import java.util.Map;

public class BlockNode<T extends Memory> extends AbstractNode<T> {
    private Block<T> block;
    private Map<String, SequenceNode<T>> partitions;

    public BlockNode(Loc loc, Block<T> block, Map<String, SequenceNode<T>> partitions) {
        super(loc);
        this.block = block;
        this.partitions = partitions;
    }

    public Map<String, SequenceNode<T>> getPartitions() {
        return this.partitions;
    }

    public void execPartition(String name, T memory, ProgramContext<T> context) {
        this.partitions.get(name).exec(memory, context);
    }

    @Override
    public void exec(T memory, ProgramContext<T> context) {
        context.check();
        context.waitForDebugger(this);
        try {
            this.block.exec(this, memory, context);
        } catch (Throwable e) {
            if(e instanceof GoldStateException) {
                throw (GoldStateException) e;
            } else {
                throw new GoldExecutionError(context.error(this.loc(), "block", this.block.name()), e);
            }
        }
    }

}
