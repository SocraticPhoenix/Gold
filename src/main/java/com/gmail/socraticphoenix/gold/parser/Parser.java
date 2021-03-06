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

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.gold.ast.Loc;
import com.gmail.socraticphoenix.gold.ast.Node;
import com.gmail.socraticphoenix.gold.ast.SequenceNode;
import com.gmail.socraticphoenix.gold.gui.HighlightInformation;
import com.gmail.socraticphoenix.gold.gui.HighlightFormat;
import com.gmail.socraticphoenix.gold.gui.HighlightScheme;
import com.gmail.socraticphoenix.gold.gui.LocRange;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.parse.CharacterStream;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Parser<T extends Memory> {
    private List<ParserComponent<T>> sequence;

    public Parser(List<ParserComponent<T>> sequence) {
        this.sequence = sequence;
    }

    public List<SequenceNode<T>> parse(String str) {
        str = clean(str);
        Loc[] locMap = buildLocMap(str);
        CharacterStream stream = new CharacterStream(str);
        List<SequenceNode<T>> nodes = new ArrayList<>();
        for (ParserComponent<T> component : this.sequence) {
            if (!stream.hasNext() || !component.isNext(stream, locMap)) {
                throw new GoldParserError("Unrecognized string", locMap[stream.index()]);
            } else {
                Loc loc = locMap[stream.index()];
                Node<T> nxt = component.next(stream, locMap);
                nodes.add(nxt instanceof SequenceNode ? (SequenceNode<T>) nxt : new SequenceNode<>(loc, Items.buildList(nxt)));
            }
        }
        return nodes;
    }

    public HighlightInformation highlight(String str, HighlightScheme scheme) {
        HighlightInformation information = new HighlightInformation();

        str = clean(str);
        Loc[] locMap = buildLocMap(str);
        CharacterStream stream = new CharacterStream(str);
        for (ParserComponent<T> component : this.sequence) {
            if (!stream.hasNext() || !component.isNext(stream, locMap)) {
                if (!stream.hasNext()) {
                    information.addHighlight(new LocRange(locMap[stream.index()], locMap[stream.index()]), scheme.getHighlight(HighlightScheme.ERROR, new HighlightFormat(null, new Color(234, 0, 13), true, false)), "Unrecognized sequence");
                    break;
                } else {
                    Loc left = locMap[stream.index()];
                    while (stream.hasNext() && !component.isNext(stream, locMap)) {
                        stream.next();
                    }
                    Loc right = locMap[stream.index()];
                    information.addHighlight(new LocRange(left, right), scheme.getHighlight(HighlightScheme.ERROR, new HighlightFormat(null, new Color(234, 0, 13), true, false)), "Unrecognized sequence");
                    if (!stream.hasNext()) {
                        break;
                    } else if (component.isNext(stream, locMap)) {
                        component.highlight(stream, information, scheme, locMap);
                    }
                }
            } else {
                component.highlight(stream, information, scheme, locMap);
            }
        }

        return information;
    }

    private Loc[] buildLocMap(String str) {
        Loc[] map = new Loc[str.length() + 1];
        //Yep, that's right, I'm building an array for every single character location in the string
        //deal with it
        int row = 0;
        int col = 0;
        int n = 0;
        for (char c : str.toCharArray()) {
            map[n] = new Loc(col, row, n);
            n++;
            col++;
            if (c == '\n') {
                row++;
                col = 0;
            }
        }
        map[n] = new Loc(col, row, n); //EOF loc
        return map;
    }

    private static String clean(String str) {
        BufferedReader reader = new BufferedReader(new StringReader(str));
        String line;
        List<String> lines = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("String reader threw IOException", e);
        }
        return String.join("\n", lines.toArray(new String[lines.size()]));
    }

}
