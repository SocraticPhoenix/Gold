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

import com.gmail.socraticphoenix.gold.program.io.Input;

import java.io.IOException;
import java.util.Stack;

public class DelegateCharacterInput implements Input {
    private Input delegate;
    private Stack<Integer> characters;

    public DelegateCharacterInput(Input delegate) {
        this.delegate = delegate;
        this.characters = new Stack<>();
    }

    @Override
    public boolean has() {
        return !this.characters.isEmpty() || this.delegate.has();
    }

    @Override
    public String get() {
        if (!this.characters.isEmpty()) {
            return new String(new int[]{this.characters.pop()}, 0, 1);
        } else {
            String next = this.delegate.get();
            if (next.isEmpty()) {
                next = "\0";
            }
            int[] pts = next.codePoints().toArray();
            for (int i = pts.length - 1; i >= 0; i--) {
                this.characters.push(pts[i]);
            }

            return new String(new int[]{this.characters.pop()}, 0, 1);
        }
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

}
