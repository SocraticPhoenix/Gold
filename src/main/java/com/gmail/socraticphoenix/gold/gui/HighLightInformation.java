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

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.collect.coupling.Triple;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HighlightInformation {
    private List<Triple<LocRange, HighlightFormat, String>> highlights;
    private Map<LocRange, List<LocRange>> rangeLinks;
    private Map<LocRange, HighlightFormat> linkStyle;

    public HighlightInformation() {
        this.highlights = new ArrayList<>();
        this.rangeLinks = new LinkedHashMap<>();
        this.linkStyle = new LinkedHashMap<>();
    }

    public void link(LocRange parent, HighlightFormat format) {
        this.rangeLinks.computeIfAbsent(parent, Items::buildList);
        this.linkStyle.put(parent, format);
    }

    public void link(LocRange parent, LocRange link) {
        this.rangeLinks.computeIfAbsent(parent, Items::buildList).add(link);
        this.linkStyle.computeIfAbsent(parent, f -> new HighlightFormat(new Color(117, 193, 190), null, false, false));
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

    public Map<LocRange, List<LocRange>> getRangeLinks() {
        return this.rangeLinks;
    }

    public Map<LocRange, HighlightFormat> getLinkStyle() {
        return this.linkStyle;
    }

}
