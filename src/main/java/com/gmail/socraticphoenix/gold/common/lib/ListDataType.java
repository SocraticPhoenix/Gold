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
package com.gmail.socraticphoenix.gold.common.lib;

import com.gmail.socraticphoenix.gold.program.value.DataType;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.NamedDataType;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternResult;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.function.Function;

public class ListDataType extends NamedDataType implements DataType {
    private Function<String, Value> defaultReader;

    public ListDataType(Function<String, Value> defaultReader) {
        super("List");
        this.defaultReader = defaultReader;
    }

    @Override
    public PatternRestriction form(DataTypeRegistry registry) {
        return (s, i, patternContext) -> {
            if (!s.substring(i).startsWith(":")) {
                return PatternResult.parseError("Expected :", i);
            }

            int brackets = 0;
            PatternRestriction innerForms = registry.innerForms();
            int j;
            for (j = i; j < s.length(); j++) {
                if(s.charAt(j) == ':') {
                    brackets++;
                } else if (s.charAt(j) == ';') {
                    brackets--;
                } else if (s.charAt(j) == '!') {
                    j++;
                    break;
                } else {
                    PatternResult match = innerForms.match(s, j, patternContext);
                    if(match.isSuccesful()) {
                        j = match.getEnd();
                    }
                }

                if(brackets == 0) {
                    j++;
                    break;
                }
            }

            return PatternResult.succesful(j);
        };
    }

    @Override
    public Value read(CharacterStream content, DataTypeRegistry registry) {
        Stack<ValueList> values = new Stack<>();
        strIter:
        while (content.hasNext()) {
            char n = content.next().get();
            if(n == ':') {
                values.push(new ValueList());
            } else if (n == ';') {
                ValueList piece = values.pop();
                values.peek().add(new Value(this, piece));
                if(values.size() == 1) {
                    break;
                }
            } else if (n == '!') {
                while (values.size() > 1) {
                    ValueList piece = values.pop();
                    values.peek().add(new Value(this, piece));
                }
                break;
            } else {
                content.back();
                for(DataType type : registry.getTypes()) {
                    if(content.isNext(registry.form(type))) {
                        values.peek().add(type.read(content, registry));
                        continue strIter;
                    }
                }

                values.peek().add(this.defaultReader.apply(content.nextUntil(';', '!')));
            }
        }

        return new Value(this, values.pop());
    }

    @Override
    public boolean canCast(Value value, DataType other, DataTypeRegistry registry) {
        if(other instanceof StringDataType) {
            return true;
        } else if (other instanceof NumberDataType) {
            return value.getAs(ValueList.class).get().stream().allMatch(v -> v.canCast(other, registry));
        }
        return false;
    }

    @Override
    public Value cast(Value value, DataType other, DataTypeRegistry registry) {
        ValueList list = value.getAs(ValueList.class).get();
        if(other instanceof StringDataType) {
            StringBuilder builder = new StringBuilder();
            for(Value val : list) {
                if(val.canCast(other, registry)) {
                    builder.append(val.cast(other, registry).getAsString().get());
                } else {
                    builder.append(val.getContent());
                }
            }
            return new Value(other, builder.toString());
        } else if (other instanceof NumberDataType) {
            BigDecimal res = BigDecimal.ZERO;
            for(Value v : value.getAs(ValueList.class).get()) {
                res = res.add(v.cast(other, registry).getAs(BigDecimal.class).get());
            }
            return new Value(this, res);
        } else {
            return value;
        }
    }

}
