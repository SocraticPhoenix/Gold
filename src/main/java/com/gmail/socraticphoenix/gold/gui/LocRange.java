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
package com.gmail.socraticphoenix.gold.gui;

import com.gmail.socraticphoenix.gold.ast.Loc;

import java.util.Objects;

public class LocRange {
    private Loc start;
    private Loc end;

    public LocRange(Loc start, Loc end) {
        this.start = start;
        this.end = end;
    }

    public int len() {
        return this.end.getIndex() - this.start.getIndex();
    }

    public Loc getStart() {
        return this.start;
    }

    public void setStart(Loc start) {
        this.start = start;
    }

    public Loc getEnd() {
        return this.end;
    }

    public void setEnd(Loc end) {
        this.end = end;
    }

    public boolean contains(Loc loc) {
        return this.contains(loc.getIndex());
    }

    public boolean contains(int index) {
        return this.start.getIndex() <= index && index < this.end.getIndex();
    }

    public boolean inclusiveContains(int index) {
        return this.start.getIndex() <= index && index <= this.end.getIndex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocRange)) return false;
        LocRange locRange = (LocRange) o;
        return Objects.equals(start, locRange.start) &&
                Objects.equals(end, locRange.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

}
