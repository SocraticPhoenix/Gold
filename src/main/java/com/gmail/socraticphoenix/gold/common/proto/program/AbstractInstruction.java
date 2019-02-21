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

import com.gmail.socraticphoenix.gold.program.Instruction;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.memory.Memory;

import java.util.List;

public abstract class AbstractInstruction<T extends Memory> implements Instruction<T> {
    private List<Argument<T>> arguments;
    private String name;
    private String id;
    private String help;
    private String doc;
    private String op;
    private boolean gensId;

    public AbstractInstruction(List<Argument<T>> arguments, String name, String id, String help, String doc, String op, boolean gensId) {
        this.arguments = arguments;
        this.name = name;
        this.id = id;
        this.help = help;
        this.doc = doc;
        this.op = op;
        this.gensId = gensId;
    }

    @Override
    public String op() {
        return this.op;
    }

    @Override
    public List<Argument<T>> arguments() {
        return this.arguments;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String help() {
        return this.help;
    }

    @Override
    public boolean gensId() {
        return this.gensId;
    }

    @Override
    public String doc() {
        return this.doc;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
