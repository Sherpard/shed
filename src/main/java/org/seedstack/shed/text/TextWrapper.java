/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.text;

/**
 * Wrap text to the specified width using a greedy algorithm. A continuation symbol can be used when
 * a long word doesn't fit on one line and must be broken apart.
 */
public class TextWrapper {
    public static final String DEFAULT_CONTINUATION = "—";
    public static final int DEFAULT_WIDTH = 80;
    public static final boolean DEFAULT_STRICT_MODE = false;
    private static final String LINE_SEPARATOR = "\n";
    private final int width;
    private final String continuation;
    private final boolean strict;

    /**
     * Creates a text wrapper with the default options.
     */
    public TextWrapper() {
        this(DEFAULT_WIDTH);
    }

    /**
     * Creates a text wrapper with a custom line width.
     *
     * @param width the line width.
     */
    public TextWrapper(int width) {
        this(width, DEFAULT_CONTINUATION);
    }

    /**
     * Creates a text wrapper with a custom line width and a custom continuation character.
     *
     * @param width        the line width.
     * @param continuation the continuation character.
     */
    public TextWrapper(int width, String continuation) {
        this(width, continuation, DEFAULT_STRICT_MODE);
    }

    /**
     * Creates a text wrapper with a custom line width, a custom continuation character and a
     * specified strict mode.
     *
     * @param width        the line width.
     * @param continuation the continuation character.
     * @param strict       if true, line width is strictly enforced, if false, single words are
     *                     allowed to go beyond.
     */
    public TextWrapper(int width, String continuation, boolean strict) {
        this.width = width;
        this.continuation = continuation;
        this.strict = strict;
    }

    /**
     * Wrap the specified text.
     *
     * @param text the text to wrap.
     * @return the wrapped text.
     */
    public String wrap(String text) {
        StringBuilder sb = new StringBuilder();
        int continuationLength = continuation.length();
        int currentPosition = 0;

        for (String word : text.split(" ")) {
            String lastWord;
            int wordLength = word.length();

            if (currentPosition + wordLength <= width) {
                if (currentPosition != 0) {
                    sb.append(" ");
                    currentPosition += 1;
                }

                sb.append(lastWord = word);
                currentPosition += wordLength;
            } else {
                if (currentPosition > 0) {
                    sb.append(LINE_SEPARATOR);
                    currentPosition = 0;
                }

                if (wordLength > width && strict) {
                    int i = 0;
                    while (i + width < wordLength) {
                        sb.append(word.substring(i, width - continuationLength)).append(continuation)
                                .append(LINE_SEPARATOR);
                        i += width - continuationLength;
                    }
                    String endOfWord = word.substring(i);
                    sb.append(lastWord = endOfWord);
                    currentPosition = endOfWord.length();
                } else {
                    sb.append(lastWord = word);
                    currentPosition += wordLength;
                }
            }

            int lastNewLine = lastWord.lastIndexOf("\n");
            if (lastNewLine != -1) {
                currentPosition = lastWord.length() - lastNewLine;
            }
        }

        return sb.toString();
    }
}
