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

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class HighlightScheme {
    public static final String INSTRUCTION = "highlight.instruction";
    public static final String BLOCK = "highlight.block";
    public static final String VALUE = "highlight.value";
    public static final String NORMAL = "highlight.normal";
    public static final String ERROR = "highlight.normal";

    private Map<String, HighlightFormat> highlights;

    public HighlightScheme() {
        this.highlights = new LinkedHashMap<>();
    }

    public boolean has(String key) {
        return this.highlights.containsKey(key);
    }

    public HighlightFormat getHighlight(String key, HighlightFormat def) {
        return this.highlights.getOrDefault(key, def);
    }

    public void setHighlight(String key, HighlightFormat def) {
        this.highlights.put(key, def);
    }

    public HighlightFormat getHighlight(String key) {
        return this.highlights.get(key);
    }

}
