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
package com.gmail.socraticphoenix.gold.gui.exec;

import com.gmail.socraticphoenix.gold.common.proto.program.io.OfferedInput;
import com.gmail.socraticphoenix.gold.gui.FormattedPane;
import com.gmail.socraticphoenix.gold.gui.GUI;
import com.gmail.socraticphoenix.gold.gui.HighlightScheme;
import com.gmail.socraticphoenix.gold.program.Language;
import com.gmail.socraticphoenix.gold.program.ProgramContext;
import com.gmail.socraticphoenix.gold.program.io.Input;
import com.gmail.socraticphoenix.gold.program.io.Output;
import com.gmail.socraticphoenix.gold.program.memory.Memory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

public class ExecGui<T extends Memory> implements GUI {
    private JPanel panel1;
    private JTextPane program;
    private JTextArea out;
    private JButton runButton;
    private JTextField in;
    private JButton terminateButton;
    private JButton clearOutputButton;

    private Language<T> language;

    private HighlightScheme scheme;
    private FormattedPane programFormatted;
    private AtomicBoolean running;
    private ProgramContext<T> context;

    private OfferedInput input;
    private Output output;

    private Input delegateIn;
    private Output delegateOut;

    public ExecGui(Language<T> language, HighlightScheme scheme, UnaryOperator<Input> inputOper, UnaryOperator<Output> outputOper) {
        this.language = language;
        this.scheme = scheme;
        this.running = new AtomicBoolean(false);
        this.input = new OfferedInput();
        this.output = new Output() {
            @Override
            public void publish(String info) {
                if (!EventQueue.isDispatchThread()) {
                    try {
                        EventQueue.invokeAndWait(() -> {
                            out.append(info);
                        });
                    } catch (InterruptedException | InvocationTargetException ignore) {

                    }
                } else {
                    out.append(info);
                }
            }

            @Override
            public void close() throws IOException {

            }
        };

        this.delegateIn = inputOper.apply(this.input);
        this.delegateOut = outputOper.apply(this.output);
        createUIComponents();
        $$$setupUI$$$();
    }

    private int n;

    public void addListeners() {
        this.in.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String txt = in.getText();
                    in.setText("");
                    output.publishln(txt);
                    input.offer(txt);
                    in.setText("");
                }
            }
        });
        this.clearOutputButton.addActionListener(a -> {
            this.out.setText("");
        });
        this.runButton.addActionListener(a -> {
            if (this.running.get()) {
                this.output.publishln("A program is already running!");
            } else {
                this.running.set(true);
                this.input.close();
                Thread thread = new Thread(() -> {
                    this.output.publishln("------------- Running Program -------------");
                    this.output.publishln();
                    try {
                        this.language.exec(this.program.getText(), c -> this.context = c, this.delegateIn, this.delegateOut);
                    } catch (Throwable e) {
                        this.output.publishln("An error occurred while running the program:");
                        e.printStackTrace(); //Print to STDERR for debug if necessary
                        Throwable th = e;
                        do {
                            this.output.publish(th.getClass().getName() + ": " + th.getMessage());
                            th = th.getCause();
                            if (th != null) {
                                this.output.publishln(", caused by: ");
                            } else {
                                this.output.publishln();
                            }
                        } while (th != null);
                    }
                    this.output.publishln();
                    this.output.publishln("------------- Program Terminated -------------");
                    this.running.set(false);
                }, "Gold-Exec" + n++);
                thread.start();
            }
        });
        this.terminateButton.addActionListener(a -> {
            if (this.running.get() && this.context != null) {
                this.context.terminate();
            }
        });
        this.programFormatted.addDefaultListeners();
    }

    private void createUIComponents() {
        this.programFormatted = new FormattedPane(this.language.getParser(), this.scheme);
        this.program = this.programFormatted;

    }

    @Override
    public void addTo(JFrame frame) {
        frame.add(this.panel1);
    }


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        program.setBackground(new Color(-1));
        Font programFont = this.$$$getFont$$$("Monospaced", Font.PLAIN, 14, program.getFont());
        if (programFont != null) program.setFont(programFont);
        scrollPane1.setViewportView(program);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        out = new JTextArea();
        out.setEditable(false);
        Font outFont = this.$$$getFont$$$("Monospaced", Font.PLAIN, 14, out.getFont());
        if (outFont != null) out.setFont(outFont);
        scrollPane2.setViewportView(out);
        runButton = new JButton();
        runButton.setText("Run");
        panel1.add(runButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        in = new JTextField();
        in.setEnabled(true);
        Font inFont = this.$$$getFont$$$("Monospaced", Font.PLAIN, 14, in.getFont());
        if (inFont != null) in.setFont(inFont);
        panel1.add(in, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Program:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Out:");
        panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("In:");
        panel1.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        terminateButton = new JButton();
        terminateButton.setText("Terminate");
        panel1.add(terminateButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearOutputButton = new JButton();
        clearOutputButton.setText("Clear Output");
        panel1.add(clearOutputButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
