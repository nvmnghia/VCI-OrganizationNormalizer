package Utilities;

import java.text.Normalizer;

public class StringNormalizer {
    /**
     * Remove all umlauts, accents, spaces and punctuations
     *
     * @param input the string to be normalized
     * @return the normalized string
     */
    public static String normalize(String input) {
        char[] out = new char[input.length()];
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        int j = 0;

        for (int i = 0, n = input.length(); i < n; ++i) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c)) {
                out[j++] = Character.toLowerCase(c);
            } else if (c != '\n' && (Character.isWhitespace(c) || Character.isDigit(c))) {
                out[j++] = c;
            }
        }

        return new String(out).replaceAll("\\s+", " ").trim();
    }

}
