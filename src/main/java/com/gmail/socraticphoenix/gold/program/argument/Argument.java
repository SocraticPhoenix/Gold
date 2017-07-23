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
package com.gmail.socraticphoenix.gold.program.argument;

import com.gmail.socraticphoenix.collect.coupling.Switch;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.gold.program.value.DataType;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Argument<T extends Memory> {
    private String name;
    private DataType[] types;
    private boolean allowCasting;
    private Function<Value, Optional<String>> check;
    private Function<Value, Value> preProcessor;
    private Function<T, Value> getter;

    public Argument(String name, DataType[] types, boolean allowCasting, Function<Value, Optional<String>> check, Function<Value, Value> preProcessor, Function<T, Value> getter) {
        this.name = name == null ? "{unnamed}" : name;
        this.types = types == null ? new DataType[0] : types;
        this.allowCasting = allowCasting;
        this.check = check == null ? v -> Optional.empty() : check;
        this.preProcessor = preProcessor == null ? v -> v : preProcessor;
        this.getter = getter == null ? m -> m.get(this) : getter;
    }

    public static <T extends Memory> Switch<Map<String, Value>, String> get(List<Argument<T>> args, T memory, DataTypeRegistry registry, ProgramContext<T> context) {
        Map<String, Value> mp = new LinkedHashMap<>();
        for (Argument<T> arg : args) {
            context.check();
            Switch<Value, String> get = arg.getMatchAndProcess(memory, registry);
            if (get.containsB()) {
                return Switch.ofB(get.getB().get());
            } else {
                mp.put(arg.getName(), get.getA().get());
            }
        }

        return Switch.ofA(mp);
    }

    public static <T extends Memory> ArgumentListBuilder<T> list() {
        return new ArgumentListBuilder<>();
    }

    public static <T extends Memory> List<Argument<T>> empty() {
        return new ArrayList<>();
    }

    public static <T extends Memory> Builder<T> custom() {
        return new Builder<>();
    }

    public static <T extends Memory> Argument<T> named(String name) {
        return new Argument<T>(name, null, false, null, null, null);
    }

    public static <T extends Memory> Argument<T> named(String name, DataType... types) {
        return new Argument<>(name, types, true, null, null, null);
    }

    public static <T extends Memory> Argument<T> any() {
        return new Argument<>(null, null, false, null, null, null);
    }

    public static <T extends Memory> Argument<T> unnamed(DataType... types) {
        return new Argument<>(null, types, false, null, null, null);
    }

    public String getName() {
        return this.name;
    }

    public DataType[] getTypes() {
        return this.types;
    }

    public boolean isAllowCasting() {
        return this.allowCasting;
    }

    public Function<Value, Optional<String>> getCheck() {
        return this.check;
    }

    public Function<Value, Value> getPreProcessor() {
        return this.preProcessor;
    }

    public Function<T, Value> getGetter() {
        return this.getter;
    }

    public boolean matches(Value value, DataTypeRegistry registry) {
        return value != null && !match(value, registry).isPresent();
    }

    public Switch<Value, String> getMatchAndProcess(T memory, DataTypeRegistry registry) {
        Value get = this.get(memory);
        Optional<String> check = this.match(get, registry);
        return check.<Switch<Value, String>>map(Switch::ofB).orElseGet(() -> Switch.ofA(this.apply(get, registry)));
    }

    public Value get(T memory) {
        return this.getter.apply(memory);
    }

    public Optional<String> match(Value value, DataTypeRegistry registry) {
        if (types.length == 0) {
            return this.check.apply(value);
        } else {
            for (DataType type : this.types) {
                if (this.allowCasting && value.canCast(type, registry)) {
                    return Optional.empty();
                } else if (value.getType().equals(type)) {
                    return Optional.empty();
                }
            }

            return Optional.of("Got type " + value.getType() + " but was expecting one of " + Arrays.toString(this.types));
        }
    }

    public Value apply(Value value, DataTypeRegistry registry) {
        DataType target = null;
        if (this.types.length == 0) {
            target = value.getType();
        } else {
            for (DataType type : this.types) {
                if (this.allowCasting && value.canCast(type, registry) && target == null) {
                    target = type;
                } else if (value.getType().equals(type)) {
                    target = type;
                    break;
                }
            }
        }
        return this.preProcessor.apply(value.cast(target, registry));
    }

    public static class Builder<T extends Memory> {
        private String name;
        private DataType[] types;
        private boolean allowCasting;
        private Function<Value, Optional<String>> check;
        private Function<Value, Value> preProcessor;
        private Function<T, Value> getter;

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> types(DataType... types) {
            this.types = types;
            return this;
        }

        public Builder<T> casting(boolean value) {
            this.allowCasting = value;
            return this;
        }

        public Builder<T> check(Function<Value, Optional<String>> check) {
            this.check = check;
            return this;
        }

        public Builder<T> process(Function<Value, Value> processor) {
            this.preProcessor = processor;
            return this;
        }

        public Builder<T> get(Function<T, Value> getter) {
            this.getter = getter;
            return this;
        }

        public Argument<T> build() {
            return new Argument<>(this.name, this.types, this.allowCasting, this.check, this.preProcessor, this.getter);
        }

        public Builder<T> reset() {
            this.name = null;
            this.types = null;
            this.allowCasting = false;
            this.check = null;
            this.preProcessor = null;
            this.getter = null;

            return this;
        }

    }

    public static class ArgumentListBuilder<T extends Memory> {
        private boolean doneAny;

        private List<Argument<T>> arguments = new ArrayList<>();
        private Builder<T> currBuilder = new Builder<>();

        public ArgumentListBuilder<T> next() {
            this.arguments.add(this.currBuilder.build());
            this.currBuilder.reset();
            this.doneAny = false;
            return this;
        }

        public ArgumentListBuilder<T> name(String name) {
            this.doneAny = true;
            this.currBuilder.name(name);
            return this;
        }

        public ArgumentListBuilder<T> types(DataType... types) {
            this.doneAny = true;
            this.currBuilder.types(types);
            return this;
        }

        public ArgumentListBuilder<T> casting(boolean value) {
            this.doneAny = true;
            this.currBuilder.casting(value);
            return this;
        }

        public ArgumentListBuilder<T> check(Function<Value, Optional<String>> check) {
            this.doneAny = true;
            this.currBuilder.check(check);
            return this;
        }

        public ArgumentListBuilder<T> process(Function<Value, Value> processor) {
            this.doneAny = true;
            this.currBuilder.process(processor);
            return this;
        }

        public ArgumentListBuilder<T> get(Function<T, Value> getter) {
            this.doneAny = true;
            this.currBuilder.get(getter);
            return this;
        }

        public List<Argument<T>> build() {
            if (this.doneAny) {
                this.arguments.add(this.currBuilder.build());
            }
            return this.arguments;
        }

        public ArgumentListBuilder<T> reset() {
            this.arguments.clear();
            this.currBuilder.reset();
            return this;
        }

    }

}
