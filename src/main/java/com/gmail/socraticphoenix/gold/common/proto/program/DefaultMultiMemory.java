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
import com.gmail.socraticphoenix.gold.program.memory.StackMemory;
import com.gmail.socraticphoenix.gold.program.memory.TapeMemory;
import com.gmail.socraticphoenix.gold.program.memory.VariableMemory;
import com.gmail.socraticphoenix.gold.program.value.Value;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMultiMemory implements StackMemory, TapeMemory, VariableMemory {
    private ProgramContext context;

    private ArrayList<Value> rightTape;
    private ArrayList<Value> leftTape;
    private int pointer;

    private Map<String, Value> variables;

    private Deque<Value> stack;
    private Value def;

    public DefaultMultiMemory(ProgramContext context, Value def) {
        this.context = context;
        this.rightTape = new ArrayList<>();
        this.leftTape = new ArrayList<>();
        this.variables = new LinkedHashMap<>();
        this.stack = new ArrayDeque<>();
        this.def = def;
    }

    @Override
    public Value pop() {
        Value tr = this.stack.poll();
        return tr == null ? this.def : tr;
    }

    @Override
    public void push(Value value) {
        this.stack.push(value);
    }

    @Override
    public void offer(Value value) {
        StackMemory.super.offer(value);
    }

    @Override
    public Value get(int pointer) {
        this.expandTo(pointer);
        return pointer < 0 ? this.leftTape.get(-pointer - 1) : this.rightTape.get(pointer);
    }

    @Override
    public void set(int pointer, Value value) {
        this.expandTo(pointer);
        if(pointer < 0) {
            this.leftTape.set(-pointer - 1, value);
        } else {
            this.rightTape.set(pointer, value);
        }
    }

    @Override
    public void to(int pointer) {
        this.pointer = pointer;
    }

    @Override
    public int pointer() {
        return this.pointer;
    }

    @Override
    public Value get(String name) {
        return this.variables.getOrDefault(name, this.def);
    }

    @Override
    public void set(String name, Value value) {
        this.variables.put(name, value);
    }

    @Override
    public Value get(Argument arg) {
        Iterator<Value> stack = this.stack.iterator();
        while (stack.hasNext()) {
            Value val = stack.next();
            if (arg.matches(val, this.context.getProgram().getDataTypeRegistry())) {
                stack.remove();
                return val;
            }
        }

        return this.context.input(arg);
    }

    @Override
    public ProgramContext context() {
        return this.context;
    }

    private void expandTo(int pointer) {
        if (pointer < 0) {
            pointer = -pointer + 1;
            while (this.leftTape.size() <= pointer) {
                this.leftTape.add(this.def);
            }
        } else {
            while (this.rightTape.size() <= pointer) {
                this.rightTape.add(this.def);
            }
        }
    }

}
