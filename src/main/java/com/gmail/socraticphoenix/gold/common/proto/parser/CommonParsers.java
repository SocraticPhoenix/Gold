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
package com.gmail.socraticphoenix.gold.common.proto.parser;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.gold.parser.BlockParserComponent;
import com.gmail.socraticphoenix.gold.parser.ChoosingParserComponent;
import com.gmail.socraticphoenix.gold.parser.FullMatchParserComponent;
import com.gmail.socraticphoenix.gold.parser.LazyParserComponent;
import com.gmail.socraticphoenix.gold.parser.Parser;
import com.gmail.socraticphoenix.gold.parser.ParserComponent;
import com.gmail.socraticphoenix.gold.parser.RepeatingParserComponent;
import com.gmail.socraticphoenix.gold.program.Block;
import com.gmail.socraticphoenix.gold.program.InstructionRegistry;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface CommonParsers {

    static <T extends Memory> Parser<T> simpleSequence(InstructionRegistry<T> instructionRegistry, DataTypeRegistry dataTypeRegistry, Predicate<Character> ignore) {
        LazyParserComponent<T> sequence = new LazyParserComponent<>();

        ParserComponent<T> instructions = instructionRegistry.instructionParser().ignoring(ignore);
        ParserComponent<T> values = dataTypeRegistry.<T>parser().ignoring(ignore);

        List<ParserComponent<T>> blockParsers = new ArrayList<>();
        for(Block<T> block : instructionRegistry.getBlocks()) {
            Map<String, ParserComponent<T>> partitions = new HashMap<>();
            for(String tag : block.tags()) {
                partitions.put(tag, sequence);
            }
            blockParsers.add(new BlockParserComponent<>(block, partitions, Items.buildList()));
        }

        ParserComponent<T> blocks = new ChoosingParserComponent<>(blockParsers).ignoring(ignore);

        ParserComponent<T> simpleSequence = new RepeatingParserComponent<>(new ChoosingParserComponent<>(Items.buildList(values, instructions, blocks)));
        sequence.set(simpleSequence);

        return new Parser<>(Items.buildList(new FullMatchParserComponent<>(simpleSequence)));
    }

}
