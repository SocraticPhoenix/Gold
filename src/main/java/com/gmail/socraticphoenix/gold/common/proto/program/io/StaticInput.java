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
package com.gmail.socraticphoenix.gold.common.proto.program.io;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.gold.program.GoldExecutionError;
import com.gmail.socraticphoenix.gold.program.io.Input;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class StaticInput implements Input {
    private String available;
    private Queue<String> input;

    public StaticInput(Queue<String> input) {
        this.input = input;
        this.available = input.stream().reduce("", (a, b) -> a + ", " + b);
    }

    public StaticInput(List<String> input) {
        this.input = new ArrayDeque<>();
        this.input.addAll(input);
    }

    public StaticInput(String... input) {
        this(Items.buildList(input));
    }

    @Override
    public boolean has() {
        if(!this.input.isEmpty()) {
            return true;
        } else {
            throw new GoldExecutionError("Attempted to get input from empty static input. Original input: [" + this.available + "]");
        }
    }

    @Override
    public String get() {
        return this.input.poll();
    }

    @Override
    public void close() throws IOException {
        this.input.clear();
    }

}
