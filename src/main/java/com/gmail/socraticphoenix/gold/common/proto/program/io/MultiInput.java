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

import com.gmail.socraticphoenix.gold.program.GoldExecutionError;
import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiInput implements Input {
    private List<Input> delegate;

    public MultiInput(List<Input> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean has() {
        return this.delegate.stream().anyMatch(i -> {
            try {
                return i.has();
            } catch (GoldExecutionError e) {
                return false;
            }
        });
    }

    @Override
    public String get() {
        for(Input i : this.delegate) {
            if(i.has()) {
                return i.get();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        List<Throwable> exceptions = new ArrayList<>();
        for(Input i : this.delegate) {
            try {
                i.close();
            } catch (Throwable e) {
                exceptions.add(e);
            }
        }

        if(!exceptions.isEmpty()) {
            IOException exception = new IOException();
            for(Throwable ex : exceptions) {
                exception.addSuppressed(ex);
            }
            throw exception;
        }
    }
}
