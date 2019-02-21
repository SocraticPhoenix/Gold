import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.gold.common.proto.parser.CommonParsers;
import com.gmail.socraticphoenix.gold.common.proto.program.CommonInstructions;
import com.gmail.socraticphoenix.gold.common.proto.program.DefaultMultiMemory;
import com.gmail.socraticphoenix.gold.common.proto.program.ObjectDataType;
import com.gmail.socraticphoenix.gold.common.proto.program.io.DelegateCharacterInput;
import com.gmail.socraticphoenix.gold.gui.DisplayableGui;
import com.gmail.socraticphoenix.gold.gui.HighlightFormat;
import com.gmail.socraticphoenix.gold.gui.HighlightScheme;
import com.gmail.socraticphoenix.gold.gui.exec.ExecGui;
import com.gmail.socraticphoenix.gold.parser.Parser;
import com.gmail.socraticphoenix.gold.program.Block;
import com.gmail.socraticphoenix.gold.program.Function;
import com.gmail.socraticphoenix.gold.program.Instruction;
import com.gmail.socraticphoenix.gold.program.InstructionRegistry;
import com.gmail.socraticphoenix.gold.program.Language;
import com.gmail.socraticphoenix.gold.program.Program;
import com.gmail.socraticphoenix.gold.program.argument.Argument;
import com.gmail.socraticphoenix.gold.program.memory.TapeMemory;
import com.gmail.socraticphoenix.gold.program.value.DataType;
import com.gmail.socraticphoenix.gold.program.value.DataTypeRegistry;
import com.gmail.socraticphoenix.gold.program.value.Value;

import java.awt.Color;
import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BF {
    private static DataType bfInt = new ObjectDataType("int32");

    //CommonInstructions.of(name, help, documentation, id, operation, arguments, executor)
    private static Instruction<TapeMemory> add = CommonInstructions.of("add", "increment the current cell", "add doc", "+", "add", Argument.empty(), (args, mem, context) -> {
        mem.set(new Value(bfInt, mem.get().getAsInteger(0) + 1));
    });

    private static Instruction<TapeMemory> sub = CommonInstructions.of("sub", "decrement the current cell", "sub doc", "-", "sub", Argument.empty(), (args, mem, context) -> {
        mem.set(new Value(bfInt, mem.get().getAsInteger(0) - 1));
    });

    private static Instruction<TapeMemory> retreat = CommonInstructions.of("retreat", "move the pointer left", "retreat doc", "<", "retreat", Argument.empty(), (args, mem, context) -> {
        mem.retreat();
    });

    private static Instruction<TapeMemory> advance = CommonInstructions.of("advance", "move the pointer right", "advance doc", ">", "advance", Argument.empty(), (args, mem, context) -> {
        mem.advance();
    });

    private static Instruction<TapeMemory> inputChar = CommonInstructions.of("input", "input a character", "input doc", ",", "input", Items.buildList(Argument.named("in")), (args, mem, context) -> {
        int in = args.get("in").getAsInteger(0);
        mem.set(new Value(bfInt, in));
    });

    private static Instruction<TapeMemory> outputChar = CommonInstructions.of("output", "output a character", "output doc", ".", "output", Argument.empty(), (args, mem, context) -> {
        context.getOutput().publish(new String(new int[]{mem.get().getAsInteger(0)}, 0, 1));
    });

    //CommonInstructions.whileLoop(name, help, documentation, operation, block tag list, condition, executor)
    private static Block<TapeMemory> whileBlock = CommonInstructions.whileLoop("while", "loop while the current cell is non-zero", "while doc", "while", Items.buildList("[", "]"), (nd, mem, context) -> {
        return mem.get().getAsInteger(0) != 0;
    }, (nd, mem, context) -> {
        nd.execPartition("[", mem, context);
    });

    public static void main(String[] args) throws IOException {
        DataTypeRegistry dataTypeRegistry = new DataTypeRegistry();
        InstructionRegistry<TapeMemory> instructionRegistry = new InstructionRegistry<>();
        dataTypeRegistry.register(bfInt);
        instructionRegistry.register(add, sub, retreat, advance, inputChar, outputChar, whileBlock);
        Parser<TapeMemory> parser = CommonParsers.simpleSequence(instructionRegistry, dataTypeRegistry, c -> !Items.contains(c, new char[]{'+', '-', '<', '>', ',', '.', '[', ']'}));

        Language<TapeMemory> bf = new Language<>(parser, list -> {
            Program<TapeMemory> program = new Program<>(() -> "main", list.stream().map(s -> new Function<>(s.loc(), "main", s)).collect(Collectors.toList()), instructionRegistry, dataTypeRegistry);
            program.setInputStrategy((in, out, arg, ctx) -> {
                if(!in.has()) {
                    out.publishln();
                    out.publish("> ");
                }
                while (!in.has()) {
                    ctx.check();
                }
                String res = in.get();
                return new Value(bfInt, res.codePoints().findFirst().getAsInt());
            });
            return program;
        }, c -> new DefaultMultiMemory(c, new Value(bfInt, 0)));

        HighlightScheme scheme = new HighlightScheme();
        scheme.setHighlight("instruction.add", new HighlightFormat(null, new Color(255, 121, 239), false, false));
        scheme.setHighlight("instruction.sub", new HighlightFormat(null, new Color(255, 121, 239), false, false));
        scheme.setHighlight("instruction.advance", new HighlightFormat(null, new Color(47, 159, 52), false, false));
        scheme.setHighlight("instruction.retreat", new HighlightFormat(null, new Color(47, 159, 52), false, false));
        scheme.setHighlight("instruction.input", new HighlightFormat(null, new Color(0, 17, 176), false, false));
        scheme.setHighlight("instruction.output", new HighlightFormat(null, new Color(0, 17, 176), false, false));
        scheme.setHighlight("block.while", new HighlightFormat(null, new Color(170, 7, 0), false, false));

        ExecGui<TapeMemory> gui = new ExecGui<>(bf, scheme, DelegateCharacterInput::new, UnaryOperator.identity());
        gui.addListeners();

        DisplayableGui displayableGui = new DisplayableGui("test", gui, 500, 500);
        displayableGui.display();
    }

}