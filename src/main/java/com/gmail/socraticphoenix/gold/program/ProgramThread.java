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

import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;
import com.gmail.socraticphoenix.gold.program.memory.Memory;

import java.util.function.Consumer;
import java.util.function.Function;

public class ProgramThread<T extends Memory> implements Runnable {
    private Input input;
    private Output output;
    private java.util.function.Function<ProgramContext<T>, T> memoryMaker;
    private boolean debug;
    private Consumer<ProgramContext<T>> contextAction;
    private Program<T> program;
    private ProgramContext<T> context;

    public ProgramThread(Program<T> program, Input input, Output output, java.util.function.Function<ProgramContext<T>, T> memoryMaker, Consumer<ProgramContext<T>> contextAction, boolean debug) {
        this.program = program;
        this.input = input;
        this.output = output;
        this.memoryMaker = memoryMaker;
        this.debug = debug;
        this.contextAction = contextAction;
    }

    @Override
    public void run() {
        this.program.exec(this.input, this.output, this.memoryMaker, c -> {
            this.context = c;
            this.contextAction.accept(c);
        }, this.debug);
    }

    public Input getInput() {
        return this.input;
    }

    public Output getOutput() {
        return this.output;
    }

    public Function<ProgramContext<T>, T> getMemoryMaker() {
        return this.memoryMaker;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public Consumer<ProgramContext<T>> getContextAction() {
        return this.contextAction;
    }

    public Program<T> getProgram() {
        return this.program;
    }

    public ProgramContext<T> getContext() {
        return this.context;
    }

}
