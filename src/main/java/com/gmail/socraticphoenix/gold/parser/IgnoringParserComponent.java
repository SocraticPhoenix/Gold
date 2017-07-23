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
package com.gmail.socraticphoenix.gold.parser;

import com.gmail.socraticphoenix.gold.ast.Loc;
import com.gmail.socraticphoenix.gold.ast.Node;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.parse.CharacterStream;

import java.util.function.Predicate;

public class IgnoringParserComponent<T extends Memory> implements ParserComponent<T> {
    private Predicate<Character> ignoring;
    private ParserComponent<T> component;

    public IgnoringParserComponent(Predicate<Character> ignoring, ParserComponent<T> component) {
        this.ignoring = ignoring;
        this.component = component;
    }

    @Override
    public boolean isNext(CharacterStream stream, Loc[] locationMap) {
        int mark = stream.index();
        stream.consumeAll(this.ignoring);
        boolean nxt = this.component.isNext(stream, locationMap);
        stream.jumpTo(mark);
        return nxt;
    }

    @Override
    public Node<T> next(CharacterStream stream, Loc[] locationMap) {
        stream.consumeAll(this.ignoring);
        Node<T> node = this.component.next(stream, locationMap);
        stream.consumeAll(this.ignoring);
        return node;
    }

}
