package fr.afpa.pompey.APIEcommerce.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;

/**
 * Utility class for generating unique SKUs for products.
 */
public class SkuGenerator {
    
    private SkuGenerator() {
        /* This utility class should not be instantiated */
    }

    private static final Random RANDOM = new Random();
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generates a SKU based on the product name. The SKU is composed of:
     * - First 3 characters of the first word (padded if less than 3 characters)
     * - First 3 characters of the second word (or random if no second word)
     * - A random 3 or 4 character alphanumeric suffix for uniqueness
     * @param productName
     * @return
     */
    public static String generateSku(String productName) {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        String normalized = Normalizer
                .normalize(productName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", "");

        String[] words = normalized.split("\\s+");

        // Ensure each part has exactly 3 characters; pad with random letters if too short
        String firstPart = padOrTruncate(words[0], 3);
        String secondPart = words.length >= 2 ? padOrTruncate(words[1], 3) : randomString(3);

        // Random 3 or 4 character suffix for uniqueness
        String thirdPart = randomString(RANDOM.nextBoolean() ? 3 : 4);

        return firstPart + "-" + secondPart + "-" + thirdPart;
    }

    /**
     * Helper method to pad or truncate a word to a specific length
     * @param word the word to pad or truncate
     * @param length the desired length
     * @return the padded or truncated word
     */
    private static String padOrTruncate(String word, int length) {
        if (word.length() >= length) {
            return word.substring(0, length);
        } else {
            return word + randomString(length - word.length());
        }
    }

    /**
     * Helper method to generate a random alphanumeric string of a given length
     * @param length the desired length
     * @return the random string
     */
    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
