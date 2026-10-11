
/*
 * Copyright (C) 2026 FontStyle Keyboard contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.anysoftkeyboard.fontstyle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UnicodeStyleConverter {
    public enum Style {
        NORMAL("Normal"),
        BOLD("Bold"),
        ITALIC("Italic"),
        BOLD_ITALIC("Bold Italic"),
        SCRIPT("Script"),
        BOLD_SCRIPT("Bold Script"),
        FRAKTUR("Fraktur"),
        BOLD_FRAKTUR("Bold Fraktur"),
        DOUBLE_STRUCK("Double-struck"),
        MONOSPACE("Monospace"),
        SANS_SERIF("Sans-serif"),
        SANS_SERIF_BOLD("Sans-serif Bold"),
        SANS_SERIF_ITALIC("Sans-serif Italic"),
        SANS_SERIF_BOLD_ITALIC("Sans-serif Bold Italic"),
        CIRCLED("Circled"),
        FULLWIDTH("Fullwidth");

        private final String displayName;

        Style(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final Map<Style, Map<Integer, Integer>> MAPPINGS =
            buildMappings();

    private UnicodeStyleConverter() {}

    public static String convert(String input, Style style) {
        if (input == null || input.isEmpty()
                || style == null || style == Style.NORMAL) {
            return input;
        }

        Map<Integer, Integer> mapping = MAPPINGS.get(style);
        if (mapping == null || mapping.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());

        for (int offset = 0; offset < input.length();) {
            int codePoint = input.codePointAt(offset);
            int replacement = mapping.containsKey(codePoint)
                    ? mapping.get(codePoint) : codePoint;

            result.appendCodePoint(replacement);
            offset += Character.charCount(codePoint);
        }

        return result.toString();
    }

    public static Style[] availableStyles() {
        return Style.values().clone();
    }

    private static Map<Style, Map<Integer, Integer>> buildMappings() {
        Map<Style, Map<Integer, Integer>> all = new LinkedHashMap<>();

        for (Style style : Style.values()) {
            all.put(style, new LinkedHashMap<Integer, Integer>());
        }

        addAlphabet(all.get(Style.BOLD), 0x1D400, 0x1D41A, null, null);
        addAlphabet(all.get(Style.ITALIC), 0x1D434, 0x1D44E,
                new int[]{'h'}, new int[]{0x210E});
        addAlphabet(all.get(Style.BOLD_ITALIC),
                0x1D468, 0x1D482, null, null);

        addAlphabet(all.get(Style.SCRIPT), 0x1D49C, 0x1D4B6,
                new int[]{'B','E','F','H','I','L','M','R','e','g','o'},
                new int[]{0x212C,0x2130,0x2131,0x210B,0x2110,
                        0x2112,0x2133,0x211B,0x212F,0x210A,0x2134});

        addAlphabet(all.get(Style.BOLD_SCRIPT),
                0x1D4D0, 0x1D4EA, null, null);

        addAlphabet(all.get(Style.FRAKTUR), 0x1D504, 0x1D51E,
                new int[]{'C','H','I','R','Z'},
                new int[]{0x212D,0x210C,0x2111,0x211C,0x2128});

        addAlphabet(all.get(Style.BOLD_FRAKTUR),
                0x1D56C, 0x1D586, null, null);

        addAlphabet(all.get(Style.DOUBLE_STRUCK), 0x1D538, 0x1D552,
                new int[]{'C','H','N','P','Q','R','Z'},
                new int[]{0x2102,0x210D,0x2115,0x2119,
                        0x211A,0x211D,0x2124});

        addAlphabet(all.get(Style.MONOSPACE),
                0x1D670, 0x1D68A, null, null);
        addAlphabet(all.get(Style.SANS_SERIF),
                0x1D5A0, 0x1D5BA, null, null);
        addAlphabet(all.get(Style.SANS_SERIF_BOLD),
                0x1D5D4, 0x1D5EE, null, null);
        addAlphabet(all.get(Style.SANS_SERIF_ITALIC),
                0x1D608, 0x1D622, null, null);
        addAlphabet(all.get(Style.SANS_SERIF_BOLD_ITALIC),
                0x1D63C, 0x1D656, null, null);

        Map<Integer, Integer> circled = all.get(Style.CIRCLED);

        for (int i = 0; i < 26; i++) {
            circled.put( 'A' + i, 0x24B6 + i);
            circled.put( 'a' + i, 0x24D0 + i);
        }

        for (int i = 1; i <= 9; i++) {
            circled.put('0' + i, 0x2460 + i - 1);
        }
        circled.put((int) '0', 0x24EA);

        Map<Integer, Integer> fullwidth = all.get(Style.FULLWIDTH);

        for (int i = 0; i < 26; i++) {
            fullwidth.put('A' + i, 0xFF21 + i);
            fullwidth.put('a' + i, 0xFF41 + i);
        }

        for (int i = 0; i <= 9; i++) {
            fullwidth.put('0' + i, 0xFF10 + i);
        }

        for (Map.Entry<Style, Map<Integer, Integer>> entry
                : all.entrySet()) {
            entry.setValue(
                    Collections.unmodifiableMap(entry.getValue()));
        }

        return Collections.unmodifiableMap(all);
    }

    private static void addAlphabet(
            Map<Integer, Integer> map,
            int upperStart,
            int lowerStart,
            int[] specialLetters,
            int[] specialCodePoints) {

        for (int i = 0; i < 26; i++) {
            map.put( 'A' + i, upperStart + i);
            map.put('a' + i, lowerStart + i);
        }

        if (specialLetters != null && specialCodePoints != null) {
            for (int i = 0; i < specialLetters.length; i++) {
                map.put(specialLetters[i], specialCodePoints[i]);
            }
        }
    }
}
