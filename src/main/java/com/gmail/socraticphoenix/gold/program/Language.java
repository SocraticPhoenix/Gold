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

import com.gmail.socraticphoenix.gold.ast.SequenceNode;
import com.gmail.socraticphoenix.gold.parser.Parser;
import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.inversey.many.Consumer4;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Language<T extends Memory> {
    private Parser<T> parser;
    private java.util.function.Function<List<SequenceNode<T>>, Program<T>> maker;
    private Consumer4<Program<T>, Consumer<ProgramContext<T>>, Input, Output> exec;
    private java.util.function.Function<ProgramContext<T>, T> memory;

    public Language(Parser<T> parser, Function<List<SequenceNode<T>>, Program<T>> maker, java.util.function.Function<ProgramContext<T>, T> memory, Consumer4<Program<T>, Consumer<ProgramContext<T>>, Input, Output> exec) {
        this.parser = parser;
        this.maker = maker;
        this.exec = exec;
        this.memory = memory;
    }

    public Language(Parser<T> parser, Function<List<SequenceNode<T>>, Program<T>> maker, java.util.function.Function<ProgramContext<T>, T> memory) {
        this(parser, maker, memory, (p, c, i, o) -> {
            p.exec(i, o, memory, c, false);
        });
    }

    public Parser<T> getParser() {
        return this.parser;
    }

    public Function<List<SequenceNode<T>>, Program<T>> getMaker() {
        return this.maker;
    }

    public Consumer4<Program<T>, Consumer<ProgramContext<T>>, Input, Output> getExec() {
        return this.exec;
    }

    public Function<ProgramContext<T>, T> getMemory() {
        return this.memory;
    }

    public void exec(List<SequenceNode<T>> nodes, Consumer<ProgramContext<T>> contextConsumer, Input input, Output output) {
        Program<T> program = this.maker.apply(nodes);
        this.exec.call(program, contextConsumer, input, output);
    }

    public void exec(String content, Consumer<ProgramContext<T>> contextConsumer, Input input, Output output) {
        this.exec(this.parser.parse(content), contextConsumer, input, output);
    }

}
