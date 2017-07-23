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
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;

import java.math.BigDecimal;

public class NumberDataType extends NamedDataType implements DataType {
    private static String[] digits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public NumberDataType() {
        super("Number");
    }

    @Override
    public PatternRestriction form(DataTypeRegistry registry) {
        PatternRestriction digs = PatternRestrictions.oneOf(digits);

        PatternRestriction intdigits = PatternRestrictions.repeating(digs);
        PatternRestriction sign = PatternRestrictions.optional(PatternRestrictions.oneOf("-", "+"));
        PatternRestriction significand = PatternRestrictions.or(
                PatternRestrictions.sequence(intdigits, PatternRestrictions.optional(PatternRestrictions.sequence(PatternRestrictions.literal("."), PatternRestrictions.optional(intdigits)))),
                PatternRestrictions.sequence(PatternRestrictions.literal("."), intdigits)
        );
        PatternRestriction exponent = PatternRestrictions.optional(PatternRestrictions.sequence(
                PatternRestrictions.oneOf("e", "E"),
                sign,
                intdigits
        ));
        return PatternRestrictions.sequence(sign, significand, exponent);
    }

    @Override
    public Value read(CharacterStream content, DataTypeRegistry registry) {
        return new Value(this, new BigDecimal(content.next(form(registry))));
    }

    @Override
    public boolean canCast(Value value, DataType other, DataTypeRegistry registry) {
        return other instanceof StringDataType || other instanceof ListDataType;
    }

    @Override
    public Value cast(Value value, DataType other, DataTypeRegistry registry) {
        if(other instanceof StringDataType) {
            return new Value(other, value.getAs(BigDecimal.class).get().toString());
        } else if (other instanceof ListDataType) {
            BigDecimal lim = value.getAs(BigDecimal.class).get();
            BigDecimal inc = BigDecimal.ONE;
            if(lim.signum() == -1) {
                inc = inc.negate();
            }

            ValueList list = new ValueList();
            for(BigDecimal i = BigDecimal.ZERO; i.compareTo(lim) < 0; i = i.add(inc)) {
                list.add(new Value(this, i));
            }
            return new Value(other, list);
        }
        return null;
    }

}
