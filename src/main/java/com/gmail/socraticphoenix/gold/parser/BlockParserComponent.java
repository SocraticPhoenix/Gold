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
import com.gmail.socraticphoenix.gold.ast.BlockNode;
import com.gmail.socraticphoenix.gold.ast.Loc;
import com.gmail.socraticphoenix.gold.ast.Node;
import com.gmail.socraticphoenix.gold.ast.SequenceNode;
import com.gmail.socraticphoenix.gold.gui.HighlightInformation;
import com.gmail.socraticphoenix.gold.gui.HighlightFormat;
import com.gmail.socraticphoenix.gold.gui.HighlightScheme;
import com.gmail.socraticphoenix.gold.gui.LocRange;
import com.gmail.socraticphoenix.gold.program.Block;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.parse.CharacterStream;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockParserComponent<T extends Memory> implements ParserComponent<T> {
    private Block<T> block;
    private Map<String, ParserComponent<T>> partitionParsers;
    private List<String> optionalPartitions;

    public BlockParserComponent(Block<T> block, Map<String, ParserComponent<T>> partitionParsers, List<String> optionalPartitions) {
        this.block = block;
        this.partitionParsers = partitionParsers;
        this.optionalPartitions = optionalPartitions;
    }

    @Override
    public boolean isNext(CharacterStream stream, Loc[] locationMap) {
        return stream.isNext(this.block.tags().get(0));
    }

    @Override
    public Node<T> next(CharacterStream stream, Loc[] locationMap) {
        int loc = stream.index();
        Map<String, SequenceNode<T>> partitions = new HashMap<>();
        String first = this.block.tags().get(0);
        stream.next(first);
        partitions.put(first, this.sequence(this.get(first, locationMap, stream).next(stream, locationMap)));
        int partition = 1;
        while (partition < this.block.tags().size() - 1) {
            String nxtTag = this.block.tags().get(partition);
            if(stream.isNext(nxtTag)) {
                partition++;
                partitions.put(nxtTag, this.sequence(this.get(nxtTag, locationMap, stream).next(stream, locationMap)));
            } else if (this.optionalPartitions.contains(nxtTag)) {
                partition++;
            } else {
                throw new GoldParserError("Non-optional block partition tag " + nxtTag + " not found while parsing block " + this.block.name(), locationMap[stream.index()]);
            }
        }
        stream.next(this.block.tags().get(partition));
        return new BlockNode<>(locationMap[loc], this.block, partitions);
    }

    @Override
    public void highlight(CharacterStream stream, HighlightInformation information, HighlightScheme scheme, Loc[] locationMap) {
        if(this.isNext(stream, locationMap)) {
            HighlightFormat format;
            if(scheme.has("block." + this.block.name())) {
                format = scheme.getHighlight("block." + this.block.name());
            } else {
                format = scheme.getHighlight(HighlightScheme.BLOCK, new HighlightFormat(null, new Color(88, 0, 234), true, false));
            }

            Loc loc = locationMap[stream.index()];
            String first = this.block.tags().get(0);
            stream.next(first);
            Loc nloc = locationMap[stream.index()];

            LocRange firstRange = new LocRange(loc, nloc);
            information.addHighlight(firstRange, format, this.block.help());
            information.link(firstRange, scheme.getHighlight(HighlightScheme.LINK, new HighlightFormat(new Color(117, 193, 190), null, false, false)));

            this.get(first, locationMap, stream).highlight(stream, information, scheme, locationMap);
            int partition = 1;
            while (partition < this.block.tags().size() - 1) {
                String nxtTag = this.block.tags().get(partition);
                if(stream.isNext(nxtTag)) {
                    Loc left = locationMap[stream.index()];
                    stream.next(nxtTag);
                    Loc right = locationMap[stream.index()];
                    LocRange nxtRange = new LocRange(left, right);
                    information.addHighlight(nxtRange, format, this.block.help());
                    information.link(firstRange, nxtRange);
                    partition++;
                    this.get(nxtTag, locationMap, stream).highlight(stream, information, scheme, locationMap);
                } else if (this.optionalPartitions.contains(nxtTag)) {
                    partition++;
                } else {
                    Loc left = locationMap[stream.index()];
                    stream.nextUntil(nxtTag);
                    Loc right = locationMap[stream.index()];
                    information.addHighlight(new LocRange(left, right), scheme.getHighlight(HighlightScheme.ERROR, new HighlightFormat(null, new Color(234, 0, 13), true, false)), stream.isNext(nxtTag) ? "Unrecognized sequence" : "Expected non-optional block partition " + nxtTag);
                    if(!stream.isNext(nxtTag)) {
                        partition++;
                    }
                }
            }
            if(stream.isNext(this.block.tags().get(partition))) {
                Loc left = locationMap[stream.index()];
                stream.next(this.block.tags().get(partition));
                Loc right = locationMap[stream.index()];
                LocRange nxtRange = new LocRange(left, right);
                information.addHighlight(nxtRange, format, this.block.help());
                information.link(firstRange, nxtRange);
            }
        }
    }

    private SequenceNode<T> sequence(Node<T> node) {
        if(node instanceof SequenceNode) {
            return (SequenceNode<T>) node;
        } else {
            return new SequenceNode<>(node.loc(), Items.buildList(node));
        }
    }

    private ParserComponent<T> get(String partition, Loc[] locationMap, CharacterStream stream) {
        if(!this.partitionParsers.containsKey(partition)) {
            throw new GoldParserError("Missing parser for partition " + partition + " in block " + block.name(), locationMap[stream.index()]);
        }

        return this.partitionParsers.get(partition);
    }

}
