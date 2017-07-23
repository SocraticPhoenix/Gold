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
import com.gmail.socraticphoenix.gold.gui.HighLightInformation;
import com.gmail.socraticphoenix.gold.gui.HighlightFormat;
import com.gmail.socraticphoenix.gold.gui.HighlightScheme;
import com.gmail.socraticphoenix.gold.gui.LocRange;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.parse.CharacterStream;

import java.awt.Color;

public class FullMatchParserComponent<T extends Memory> implements ParserComponent<T> {
    private ParserComponent<T> component;

    public FullMatchParserComponent(ParserComponent<T> component) {
        this.component = component;
    }

    @Override
    public boolean isNext(CharacterStream stream, Loc[] locationMap) {
        return this.component.isNext(stream, locationMap);
    }

    @Override
    public Node<T> next(CharacterStream stream, Loc[] locationMap) {
        Node<T> nxt = this.component.next(stream, locationMap);
        if(stream.hasNext()) {
            throw new GoldParserError("Unrecognized sequence", locationMap[stream.index()]);
        }
        return nxt;
    }

    @Override
    public void highlight(CharacterStream stream, HighLightInformation information, HighlightScheme scheme, Loc[] locationMap) {
        if(this.component.isNext(stream, locationMap)) {
            this.component.highlight(stream, information, scheme, locationMap);
        }

        if(stream.hasNext()) {
            Loc left = locationMap[stream.index()];
            while (stream.hasNext() && !this.component.isNext(stream, locationMap)) {
                stream.next();
            }
            Loc right = locationMap[stream.index()];
            information.addHighlight(new LocRange(left, right), scheme.getHighlight(HighlightScheme.ERROR, new HighlightFormat(null, new Color(234, 0, 13), true, false)), "Unrecognized sequence");

            if(this.component.isNext(stream, locationMap)) {
                this.component.highlight(stream, information, scheme, locationMap);
            }
        }
    }

}
