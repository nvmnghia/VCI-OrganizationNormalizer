package Utl;

import java.text.Normalizer;

public class StringNormalizer {
    /**
     * Remove all umlauts, accents, spaces and punctuations
     * Convert all capital character to its lower case
     *
     * @param input the string to be normalized
     * @return the normalized string
     */
    public static String normalize(String input) {
        char[] out = new char[input.length()];
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        int j = 0;

        // Normalization separates ASCII characters and umlauts, not removing umlauts
        // This for loop remove these dirty umlauts
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c) && ! Character.isWhitespace(c)) {
                try {
                    out[j++] = Character.toLowerCase(c);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(input + "   " + (new String(out)));
                }
            } else if (c != '\n' && (Character.isWhitespace(c) || Character.isDigit(c))) {
                out[j++] = c;
            }
        }

        // Fix đ to d
        // The - in this letter is NOT an umlaut: it is a part of the character
        // Remove multiple spaces and trailing spaces
        return new String(out).replace('đ', 'd').replaceAll("\\s+", " ").trim();
    }

}
