package dmytro.algo;

public final class RotN {

    public static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuwxyz";
    private static final String ALPHABET_UPPERCASE = ALPHABET_LOWERCASE.toUpperCase();

    public static String encode(String message, int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number for encoding must be positive.");
        }

        StringBuilder resultBuilder = new StringBuilder();
        for (char c : message.toCharArray()) {
            int index = ALPHABET_LOWERCASE.indexOf(c);

            if (index != -1) {
                c = rotate(index, number, ALPHABET_LOWERCASE);
            } else {
                index = ALPHABET_UPPERCASE.indexOf(c);
                if (index != -1) {
                    c = rotate(index, number, ALPHABET_UPPERCASE);
                }
            }
            resultBuilder.append(c);
        }
        return resultBuilder.toString();
    }

    public static String decode(String message, int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number for decoding must be positive.");
        }
        StringBuilder resultBuilder = new StringBuilder();
        for (char c : message.toCharArray()) {
            int index = ALPHABET_LOWERCASE.indexOf(c);

            if (index != -1) {
                c = rotateBackward(index, number, ALPHABET_LOWERCASE);
            } else {
                index = ALPHABET_UPPERCASE.indexOf(c);
                if (index != -1) {
                    c = rotateBackward(index, number, ALPHABET_UPPERCASE);
                }
            }
            resultBuilder.append(c);
        }
        return resultBuilder.toString();
    }

    private static char rotate(int index, int number, String alphabet) {
        int newIndex = index + number;
        int maxIndex = alphabet.length() - 1;
        while (newIndex > maxIndex) {
            newIndex -= maxIndex;
        }
        return alphabet.charAt(newIndex);
    }

    private static char rotateBackward(int index, int number, String alphabet) {
        int newIndex = index - number;
        int maxIndex = alphabet.length() - 1;
        while (newIndex < 0) {
            newIndex += maxIndex;
        }
        return alphabet.charAt(newIndex);
    }
}
