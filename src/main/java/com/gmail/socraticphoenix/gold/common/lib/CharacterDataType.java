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
import com.gmail.socraticphoenix.gold.program.value.SourceOnlyDataType;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.Strings;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;

public class CharacterDataType extends NamedDataType implements SourceOnlyDataType {
    private static String[] escapes = new String[] {"0", "b", "t", "n", "f", "r", "\\", "\"", "\'"};
    private static String[] uDigits = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "a", "b", "c", "d", "e", "f"};

    public CharacterDataType() {
        super("Character");
    }

    @Override
    public PatternRestriction form(DataTypeRegistry registry) {
        PatternRestriction escape = PatternRestrictions.sequence(PatternRestrictions.literal("\\"), PatternRestrictions.oneOf(escapes));
        PatternRestriction unicode = PatternRestrictions.sequence(PatternRestrictions.literal("\\u"), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits), PatternRestrictions.oneOf(uDigits));
        return PatternRestrictions.sequence(
                PatternRestrictions.literal("'"),
                PatternRestrictions.or(escape, unicode, PatternRestrictions.length(1))
        );
    }

    @Override
    public Value read(CharacterStream content, DataTypeRegistry registry) {
        return new Value(CommonDataTypes.STRING, Strings.deEscape(Strings.cutFirst(content.next(form(registry)))));
    }

}