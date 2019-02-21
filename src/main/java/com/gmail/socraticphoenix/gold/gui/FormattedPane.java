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
package com.gmail.socraticphoenix.gold.gui;

import com.gmail.socraticphoenix.gold.parser.Parser;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.UndoManager;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class FormattedPane extends JTextPane {
    private Parser parser;
    private HighlightScheme scheme;

    private HighlightInformation currentInfo;

    public FormattedPane() {
        this(null, null);
    }

    public FormattedPane(Parser parser, HighlightScheme scheme) {
        this.parser = parser;
        this.scheme = scheme;
    }

    public void addDefaultListeners() {
        this.addCaretListener(e -> this.formatLinkageLater(e.getDot()));
        UndoManager undo = new UndoManager();
        undo.setLimit(10_000);
        this.getStyledDocument().addUndoableEditListener(undo);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                formatTextLater();
                formatLinkageLater(getCaretPosition());
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setToolTip(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setToolTip(e);
            }

        });
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.getUI().getPreferredSize(this).width <= this.getParent().getSize().width;
    }

    public void formatTextLater() {
        EventQueue.invokeLater(this::formatText);
    }

    public void formatText() {
        String text = this.getText();
        if (!text.isEmpty()) {
            this.getStyledDocument().setCharacterAttributes(0, this.getStyledDocument().getLength(), StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE), true);

            HighlightInformation information = this.parser.highlight(text, this.scheme);
            this.currentInfo = information;
            information.getHighlights().forEach(t -> {
                LocRange range = t.getA();
                HighlightFormat format = t.getB();
                SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setBold(set, format.isBold());
                StyleConstants.setItalic(set, format.isItalic());
                if (format.getHighlight() != null) {
                    StyleConstants.setBackground(set, format.getHighlight());
                }

                if (format.getText() != null) {
                    StyleConstants.setForeground(set, format.getText());
                }

                this.getStyledDocument().setCharacterAttributes(range.getStart().getIndex(), range.len(), set, true);
            });
        }
    }

    public void formatLinkageLater(int index) {
        EventQueue.invokeLater(() -> this.formatLinkage(index));
    }

    private List<Object> linkageHighlights = new ArrayList<>();

    public void formatLinkage(int index) {
        if(this.currentInfo != null) {
            linkageHighlights.forEach(this.getHighlighter()::removeHighlight);
            linkageHighlights.clear();
            this.currentInfo.getRangeLinks().values().stream().filter(l -> !l.isEmpty() && l.stream().anyMatch(r -> r.inclusiveContains(index))).forEach(links -> {
                HighlightFormat format = this.currentInfo.getLinkStyle().get(links.get(0));
                if (format != null && format.getHighlight() != null) {
                    links.forEach(range -> {
                        try {
                            linkageHighlights.add(this.getHighlighter().addHighlight(range.getStart().getIndex(), range.getEnd().getIndex(), new DefaultHighlightPainter(format.getHighlight())));
                        } catch (BadLocationException ignore) {

                        }
                    });
                }
            });
        }
    }

    public void setToolTip(MouseEvent event) {
        List<String> text = new ArrayList<>();
        int index = this.viewToModel(event.getPoint());
        if(this.currentInfo != null) {
            this.currentInfo.getHighlights().stream().filter(t -> t.getA().contains(index) && t.getC() != null).forEach(t -> {
                text.add(t.getC());
            });
            setToolTipText(text.isEmpty() ? null : String.join("; ", text.toArray(new String[text.size()])));
        } else {
            setToolTipText(null);
        }
    }

}
