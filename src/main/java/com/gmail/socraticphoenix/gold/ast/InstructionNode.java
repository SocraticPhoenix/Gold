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

import com.gmail.socraticphoenix.collect.coupling.Switch;
import com.gmail.socraticphoenix.gold.program.GoldExecutionError;
import com.gmail.socraticphoenix.gold.program.GoldStateException;
import com.gmail.socraticphoenix.gold.program.Instruction;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.Value;

import java.util.Map;

public class InstructionNode<T extends Memory> extends AbstractNode<T> implements Node<T> {
    private Instruction<T> instruction;

    public InstructionNode(Loc loc, Instruction<T> instruction) {
        super(loc);
        this.instruction = instruction;
    }

    @Override
    public void exec(T memory, ProgramContext<T> context) {
        context.check();
        context.waitForDebugger(this);
        Switch<Map<String, Value>, String> get = Argument.get(this.instruction.arguments(), memory, context.getProgram().getDataTypeRegistry(), context);
        if(get.containsB()) {
            throw new GoldExecutionError(context.error(this.loc(), "instruction", this.instruction.name(), "Failed to get arguments: " + get.getB().get()));
        } else {
            try {
                context.check();
                this.instruction.exec(get.getA().get(), memory, context);
            } catch (Throwable e) {
                if(e instanceof GoldStateException) {
                    throw (GoldStateException) e;
                } else {
                    throw new GoldExecutionError(context.error(this.loc(), "instruction", this.instruction.name()), e);
                }
            }
        }
    }

}
