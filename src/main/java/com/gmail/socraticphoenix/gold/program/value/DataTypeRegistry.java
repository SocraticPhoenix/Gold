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
package com.gmail.socraticphoenix.gold.program.value;

import com.gmail.socraticphoenix.gold.common.lib.CommonDataTypes;
import com.gmail.socraticphoenix.gold.parser.ChoosingParserComponent;
import com.gmail.socraticphoenix.gold.parser.DataTypeParserComponent;
import com.gmail.socraticphoenix.gold.parser.ParserComponent;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;

import java.util.ArrayList;
import java.util.List;

public class DataTypeRegistry {
    private List<DataType> types = new ArrayList<>();
    private List<PatternRestriction> forms = new ArrayList<>();
    private List<PatternRestriction> innerForms = new ArrayList<>();

    public <T extends Memory> ParserComponent<T> parser() {
        List<ParserComponent<T>> components = new ArrayList<>();
        for(DataType type : this.types) {
            components.add(new DataTypeParserComponent<>(type, this));
        }
        return new ChoosingParserComponent<>(components);
    }

    public void register(DataType... types) {
        for(DataType type : types) {
            this.register(types);
        }
    }

    public void register(DataType type) {
        this.types.add(type);
        this.forms.add(type.form(this));
        this.innerForms.add(type.innerForm(this));
    }

    public void registerCommon() {
        register(CommonDataTypes.CHAR);
        register(CommonDataTypes.NUMBER);
        register(CommonDataTypes.STRING);
        register(CommonDataTypes.LIST);
    }

    public Value parse(String str) {
        CharacterStream stream = new CharacterStream(str);
        for(DataType type : types) {
            PatternRestriction form = PatternRestrictions.completed(type.form(this));
            if(form.match(str).isSuccesful()) {
                return type.read(stream, this);
            }
        }

        return null;
    }

    public PatternRestriction form(DataType type) {
        PatternRestriction form = type.form(this);
        return PatternRestrictions.sequence(
                PatternRestrictions.set("INTERNAL:forms", PatternRestrictions.or(forms.toArray(new PatternRestriction[forms.size()]))),
                PatternRestrictions.set("INTERNAL:inner_forms", PatternRestrictions.or(innerForms.toArray(new PatternRestriction[innerForms.size()]))),
                form
        );
    }

    public PatternRestriction innerForm(DataType type) {
        PatternRestriction form = type.innerForm(this);
        return PatternRestrictions.sequence(
                PatternRestrictions.set("INTERNAL:forms", PatternRestrictions.or(forms.toArray(new PatternRestriction[forms.size()]))),
                PatternRestrictions.set("INTERNAL:inner_forms", PatternRestrictions.or(innerForms.toArray(new PatternRestriction[innerForms.size()]))),
                form
        );
    }

    public List<DataType> getTypes() {
        return types;
    }

    public List<PatternRestriction> getForms() {
        return forms;
    }

    public List<PatternRestriction> getInnerForms() {
        return innerForms;
    }

    public PatternRestriction innerForms() {
        return PatternRestrictions.lazy("INTERNAL:inner_forms");
    }

    public PatternRestriction forms() {
        return PatternRestrictions.lazy("INTERNAL:forms");
    }

}
