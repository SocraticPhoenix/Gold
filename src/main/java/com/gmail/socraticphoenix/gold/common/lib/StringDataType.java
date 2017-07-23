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
import com.gmail.socraticphoenix.parse.Strings;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class StringDataType extends NamedDataType implements DataType {
    private static String[] escapes = new String[] {"0", "b", "t", "n", "f", "r", "\\", "\"", "\'"};
    private static String[] uDigits = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "a", "b", "c", "d", "e", "f"};

    public StringDataType() {
        super("String");
    }

    @Override
    public PatternRestriction form(DataTypeRegistry registry) {
        PatternRestriction escape = PatternRestrictions.sequence(PatternRestrictions.literal("\\"), PatternRestrictions.oneOf(escapes));
        PatternRestriction unicode = PatternRestrictions.sequence(PatternRestrictions.literal("\\u"), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits));
        return PatternRestrictions.sequence(
                PatternRestrictions.literal("\""),
                PatternRestrictions.repeatingOrNoneNonGreedy(PatternRestrictions.or(escape, unicode, PatternRestrictions.length(1)), PatternRestrictions.literal("\""))
        );
    }

    @Override
    public Value read(CharacterStream content, DataTypeRegistry registry) {
        return new Value(this, Strings.deEscape(Strings.cutFirst(Strings.cutLast(content.next(form(registry))))));
    }

    @Override
    public boolean canCast(Value value, DataType other, DataTypeRegistry registry) {
        if(other instanceof ListDataType) {
            return true;
        } else if (other instanceof NumberDataType) {
            try {
                new BigDecimal(value.getAsString().get());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Value cast(Value value, DataType other, DataTypeRegistry registry) {
        if(other instanceof ListDataType) {
            ValueList list = value.getAsString().get().codePoints().mapToObj(c -> new Value(this, new String(new int[]{c}, 0, 1))).collect(Collectors.toCollection(ValueList::new));
            return new Value(other, list);
        } else if (other instanceof NumberDataType) {
            return new Value(other, new BigDecimal(value.getAsString().get()));
        }
        return null;
    }

}
