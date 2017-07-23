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

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.collect.coupling.Triple;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class HighLightInformation {
    private List<Triple<LocRange, HighlightFormat, String>> highlights;

    public HighLightInformation() {
        this.highlights = new ArrayList<>();
    }

    public void addHighlight(LocRange range, HighlightFormat format) {
        this.highlights.add(Triple.of(range, format, null));
    }

    public void addHighlight(LocRange range, HighlightFormat format, String tooltip) {
        this.highlights.add(Triple.of(range, format, tooltip));
    }

    public List<Triple<LocRange, HighlightFormat, String>> getHighlights() {
        return this.highlights;
    }


}
