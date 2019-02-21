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

import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.inversey.many.Function4;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Program<T extends Memory> {
    private Map<String, Function<T>> functions;
    private String mainName;
    private InstructionRegistry instructionRegistry;
    private DataTypeRegistry dataTypeRegistry;
    private Function4<Input, Output, Argument<T>, ProgramContext<T>, Value> inputStrategy;

    public Program(Map<String, Function<T>> functions, String mainName, InstructionRegistry instructionRegistry, DataTypeRegistry dataTypeRegistry) {
        this.functions = functions;
        this.mainName = mainName;
        this.instructionRegistry = instructionRegistry;
        this.dataTypeRegistry = dataTypeRegistry;
    }

    public Program(Supplier<String> nameMaker, List<Function<T>> functions, Function<T> main, String mainName, InstructionRegistry instructionRegistry, DataTypeRegistry dataTypeRegistry) {
        Map<String, Function<T>> map = new LinkedHashMap<>();
        functions.forEach(f -> map.put(nameMaker.get(), f));
        map.put(mainName, main);
        this.functions = map;
        this.mainName = mainName;
        this.instructionRegistry = instructionRegistry;
        this.dataTypeRegistry = dataTypeRegistry;
    }

    public Program(Supplier<String> nameMaker, List<Function<T>> functions, InstructionRegistry instructionRegistry, DataTypeRegistry dataTypeRegistry) {
        this(nameMaker, functions.subList(0, functions.size()), functions.get(functions.size() - 1), nameMaker.get(), instructionRegistry, dataTypeRegistry);
    }

    public String getMainName() {
        return this.mainName;
    }

    public InstructionRegistry getInstructionRegistry() {
        return this.instructionRegistry;
    }

    public DataTypeRegistry getDataTypeRegistry() {
        return this.dataTypeRegistry;
    }

    public void setInputStrategy(Function4<Input, Output, Argument<T>, ProgramContext<T>, Value> inputStrategy) {
        this.inputStrategy = inputStrategy;
    }

    public void exec(Input input, Output output, java.util.function.Function<ProgramContext<T>, T> memoryMaker, Consumer<ProgramContext<T>> contextAction, boolean debug) {
        ProgramContext<T> context = new ProgramContext<>(this, input, output, debug);
        if(this.inputStrategy != null) {
            context.setInputStrategy(this.inputStrategy);
        }
        contextAction.accept(context);
        T memory = memoryMaker.apply(context);
        context.setMemory(memory);
        this.getMain().exec(memory, context);
    }

    public void exec(Input input, Output output, java.util.function.Function<ProgramContext<T>, T> memoryMaker) {
        this.exec(input, output, memoryMaker, c->{}, false);
    }

    public static Supplier<String> charNameMaker(char start) {
        return new Supplier<String>() {
            private char current = start;

            @Override
            public String get() {
                return String.valueOf(this.current++);
            }
        };
    }

    public static Supplier<String> nameMakerWithMain(Supplier<String> nameMaker, String mainName) {
        return new Supplier<String>() {
            private boolean first = true;

            @Override
            public String get() {
                if(this.first) {
                    this.first = false;
                    return mainName;
                } else {
                    return nameMaker.get();
                }
            }
        };
    }

    public Map<String, Function<T>> getFunctions() {
        return this.functions;
    }

    public Function<T> getMain() {
        return this.get(this.mainName);
    }

    public Function<T> get(String name) {
        return this.functions.get(name);
    }

}
