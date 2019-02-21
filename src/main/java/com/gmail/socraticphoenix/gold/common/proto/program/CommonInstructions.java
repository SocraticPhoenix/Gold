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

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.gold.ast.BlockNode;
import com.gmail.socraticphoenix.gold.common.lib.ValueList;
import com.gmail.socraticphoenix.gold.program.Block;
import com.gmail.socraticphoenix.gold.program.Function;
import com.gmail.socraticphoenix.gold.program.GoldExecutionError;
import com.gmail.socraticphoenix.gold.program.Instruction;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.memory.VariableMemory;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.inversey.many.Consumer3;
import com.gmail.socraticphoenix.inversey.many.Function3;

import java.util.List;
import java.util.Map;

public interface CommonInstructions {

    static <T extends VariableMemory> Instruction<T> setVariable(String varName, boolean gensId) {
        return new SimpleInstruction<>("set[" + varName + "]", "set[" + varName + "]", "Sets " + varName, "Retrieves the first available value from memory and sets the variable " + varName + " to it.", "set " + varName + " = ${arg}", gensId, Items.buildList(Argument.named("arg")), (args, mem, context) -> {
            mem.set(varName, args.get("arg"));
        });
    }

    static <T extends VariableMemory> Instruction<T> getVariable(String varName, boolean gensId) {
        return new SimpleInstruction<>("get[" + varName + "]", "get[" + varName + "]", "Gets " + varName, "Gets the variable " + varName + " and offers it to memory.", "get " + varName, gensId, Argument.empty(), (args, mem, context) -> {
            mem.offer(mem.get(varName));
        });
    }

    static <T extends Memory> Instruction<T> callFunction(String funcName, boolean gensId) {
        return new SimpleInstruction<>("call[" + funcName + "]", "call[" + funcName + "]", "Calls " + funcName, "Calls the function " + funcName + ".", "call " + funcName, gensId, Argument.empty(), (args, mem, context) -> {
            Function<T> func = context.getProgram().getFunctions().get(funcName);
            if (func == null) {
                throw new GoldExecutionError("Unknown function: " + funcName);
            } else {
                func.exec(mem, context);
            }
        });
    }

    static <T extends Memory> Instruction<T> of(String name, String help, String doc, String id, String op, List<Argument<T>> arguments, Consumer3<Map<String, Value>, T, ProgramContext<T>> action) {
        return new SimpleInstruction<>(name, id, help, doc, op, true, arguments, action);
    }

    static <T extends Memory> Block<T> of(String name, String help, String doc, String op, List<String> tags, Consumer3<BlockNode<T>, T, ProgramContext<T>> action) {
        return new SimpleBlock<>(tags, name, help, op, doc, action);
    }

    static <T extends Memory> Block<T> conditional(String name, String help, String doc, String op, List<String> tags, Function3<BlockNode<T>, T, ProgramContext<T>, Boolean> condition, Consumer3<BlockNode<T>, T, ProgramContext<T>> action) {
        return new SimpleBlock<>(tags, name, help, doc, op, (nd, mem, context) -> {
            if (condition.invoke(nd, mem, context)) {
                action.call(nd, mem, context);
            }
        });
    }

    static <T extends Memory> Block<T> whileLoop(String name, String help, String doc, String op, List<String> tags, Function3<BlockNode<T>, T, ProgramContext<T>, Boolean> condition, Consumer3<BlockNode<T>, T, ProgramContext<T>> action) {
        return new SimpleBlock<>(tags, name, help, doc, op, (nd, mem, context) -> {
            while (condition.invoke(nd, mem, context)) {
                action.call(nd, mem, context);
                context.check();
            }
        });
    }

    static <T extends Memory> Block<T> doWhileLoop(String name, String help, String doc, String op, List<String> tags, Function3<BlockNode<T>, T, ProgramContext<T>, Boolean> condition, Consumer3<BlockNode<T>, T, ProgramContext<T>> action) {
        return new SimpleBlock<>(tags, name, help, doc, op, (nd, mem, context) -> {
            do {
                action.call(nd, mem, context);
                context.check();
            } while (condition.invoke(nd, mem, context));
        });
    }

    static <T extends Memory> Block<T> iterativeLoop(String name, String help, String doc, String op, List<String> tags, Function3<BlockNode<T>, T, ProgramContext<T>, ValueList> iterable, Consumer3<BlockNode<T>, T, ProgramContext<T>> action) {
        return new SimpleBlock<>(tags, name, help, doc, op, (nd, mem, context) -> {
            ValueList iter = iterable.invoke(nd, mem, context);
            for(Value v : iter) {
                mem.offer(v);
                action.call(nd, mem, context);
                context.check();
            }
        });
    }

}
