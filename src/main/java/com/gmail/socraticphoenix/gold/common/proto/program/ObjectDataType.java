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
package com.gmail.socraticphoenix.gold.common.proto.program;

import com.gmail.socraticphoenix.gold.program.value.DataType;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.MemoryOnlyDataType;
import com.gmail.socraticphoenix.gold.program.value.NamedDataType;
import com.gmail.socraticphoenix.gold.program.value.Value;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternResult;

public class ObjectDataType extends NamedDataType implements MemoryOnlyDataType {

    public ObjectDataType(String name) {
        super(name);
    }

    @Override
    public boolean canCast(Value value, DataType other, DataTypeRegistry registry) {
        return false;
    }

    @Override
    public Value cast(Value value, DataType other, DataTypeRegistry registry) {
        return null;
    }

}
