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
package com.gmail.socraticphoenix.gold.program;

import com.gmail.socraticphoenix.gold.parser.ChoosingParserComponent;
import com.gmail.socraticphoenix.gold.parser.InstructionParserComponent;
import com.gmail.socraticphoenix.gold.parser.ParserComponent;
import com.gmail.socraticphoenix.gold.program.memory.Memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InstructionRegistry<T extends Memory> {
    private Map<String, Instruction<T>> instructions = new LinkedHashMap<>();
    private Map<String, Block<T>> blocks = new LinkedHashMap<>();
    private java.util.function.Function<Instruction<T>, String> instIdFactory;

    public ParserComponent<T> instructionParser() {
        List<ParserComponent<T>> components = new ArrayList<>();
        for(Instruction<T> instruction : this.getInstructions()) {
            components.add(new InstructionParserComponent<>(instruction));
        }
        return new ChoosingParserComponent<>(components);
    }

    public Collection<Instruction<T>> getInstructions() {
        return this.instructions.values();
    }

    public Collection<Block<T>> getBlocks() {
        return this.blocks.values();
    }

    @SafeVarargs
    public final void register(ProgramUnit<T>... units) {
        for(ProgramUnit<T> unit : units) {
            if(unit instanceof Instruction) {
                register((Instruction<T>) unit);
            } else if (unit instanceof Block) {
                register((Block<T>) unit);
            }
        }
    }

    private void register(Instruction<T> instruction) {
        if (instruction.gensId() && this.instIdFactory != null) {
            instruction.setId(this.instIdFactory.apply(instruction));
        }
        String id = instruction.id();
        this.checkDups(id, instruction.name());
        this.instructions.put(id, instruction);
    }

    private void register(Block<T> block) {
        List<String> tags = block.tags();
        if(tags.size() <= 1) {
            throw new IllegalArgumentException("Blocks must have at least two tags, but got " + tags.size() + " instead");
        }
        String id = tags.get(0);
        this.checkDups(id, block.name());
        this.blocks.put(id, block);
    }

    private void checkDups(String id, String name) {
        if (this.instructions.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate instruction id " + id + " while attempting to register instruction/block " + name);
        }

        for(Block<T> existing : this.getBlocks()) {
            if(existing.tags().contains(id)) {
                throw new IllegalArgumentException("Instruction/block " + name + " clashes with block tag " + id + " in block " + existing.name());
            }
        }
    }

    public void setIdFactory(java.util.function.Function<Instruction<T>, String> instIdFactor) {
        this.instIdFactory = instIdFactor;
    }

}
