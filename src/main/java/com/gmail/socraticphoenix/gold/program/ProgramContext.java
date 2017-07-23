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

import com.gmail.socraticphoenix.collect.container.ResettingBoolean;
import com.gmail.socraticphoenix.gold.ast.Loc;
import com.gmail.socraticphoenix.gold.ast.Node;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.inversey.many.Function3;
import com.gmail.socraticphoenix.parse.StringFormat;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class ProgramContext<T extends Memory> {
    private static StringFormat fmt1 = StringFormat.fromString("Error while executing ${kind} ${name} at (${line}, ${col}): ${error}");
    private static StringFormat fmt2 = StringFormat.fromString("Error while executing ${kind} ${name} at (${line}, ${col})");

    private volatile boolean running;

    private Program<T> program;

    private Input input;
    private Output output;

    private Function3<Input, Output, Argument<T>, Value> inputStrategy;

    private AtomicBoolean debug;
    private boolean debugActive;
    private Node<T> debugging;

    private T memory;

    public ProgramContext(Program<T> program, Input input, Output output, boolean debugMode) {
        this.running = true;
        this.input = input;
        this.output = output;
        this.program = program;
        this.debug = new AtomicBoolean(true);
        this.debugActive = debugMode;
        this.inputStrategy = (inpt, outpt, argument) -> {
            Value value;
            do {
                this.output.publish("Enter a value> ");
                while (!this.input.has()) {
                    this.check();
                }
                this.check();
                String in = this.input.get();
                value = this.program.getDataTypeRegistry().parse(in);
                if(value == null) {
                    this.output.publishln("Unrecognized value: " + in);
                } else {
                    Optional<String> mtch = argument.match(value, this.program.getDataTypeRegistry());
                    if(mtch.isPresent()) {
                        this.output.publishln(mtch.get());
                    } else {
                        return value;
                    }
                }
                this.check();
            } while (argument.matches(value, this.program.getDataTypeRegistry()));

            throw new GoldExecutionError("Input interrupted");
        };
    }

    public T getMemory() {
        return this.memory;
    }

    void setMemory(T memory) {
        this.memory = memory;
    }

    public void waitForDebugger(Node<T> current) {
        if(this.debugActive) {
            this.debugging = current;
            while (this.debug.get());
            this.debug.set(true);
        }
    }

    public Node<T> getDebugging() {
        return this.debugging;
    }

    public void stepDebugger() {
        this.debug.set(false);
    }

    public Program<T> getProgram() {
        return this.program;
    }

    public void terminate() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void check() {
        if(!isRunning()) {
            throw new GoldExecutionError("Execution halted by user");
        }
    }

    public Input getInput() {
        return this.input;
    }

    public Output getOutput() {
        return this.output;
    }

    public String error(Loc location, String kind, String name, String content) {
        return fmt1.filler().var("name", name).var("kind", kind).var("line", location.getRow()).var("col", location.getCol()).var("error", content).fill();
    }

    public String error(Loc location, String kind, String name) {
        return fmt2.filler().var("name", name).var("kind", kind).var("line", location.getRow()).var("col", location.getCol()).fill();
    }

    public Value input(Argument argument) {
        return this.inputStrategy.invoke(this.input, this.output, argument);
    }

    public void setInputStrategy(Function3<Input, Output, Argument<T>, Value> inputStrategy) {
        this.inputStrategy = inputStrategy;
    }

}
