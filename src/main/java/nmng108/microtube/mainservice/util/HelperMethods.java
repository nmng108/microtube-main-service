package nmng108.microtube.mainservice.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

//@Configuration
//@Getter
public class HelperMethods {
//    private final String globalDateFormat;
//    private final String globalTimeFormat;
//    private final String globalDateTimeFormat;

//    public HelperMethods(@Value("${app.config.date-format:yyyy-MM-dd}") String globalDateFormat,
//                         @Value("${app.config.time-format:HH:mm:ss}") String globalTimeFormat,
//                         @Value("${app.config.datetime-format:yyyy-MM-dd HH:mm:ss}") String globalDateTimeFormat) {
//        this.globalDateFormat = globalDateFormat;
//        this.globalTimeFormat = globalTimeFormat;
//        this.globalDateTimeFormat = globalDateTimeFormat;
//    }

    /**
     * Finds the first non-null value (from the start of array) and returns that value.
     *
     * @return the first non-null value.
     */
    @SafeVarargs
    public static <T> T nullableCoalesce(T... args) {
        for (T arg : args) {
            if (arg != null) {
                return arg;
            }
        }

        return null;
    }

    /**
     * Finds the first non-null value (from the start of array) and returns that value.
     *
     * @return the first non-null value.
     */
    @SafeVarargs
    public static <T> T nonNullableCoalesce(T... args) throws IllegalArgumentException {
        for (T arg : args) {
            if (arg != null) {
                return arg;
            }
        }

        throw new IllegalArgumentException("No non-null value found");
    }

    public static String getRandomString(int length) {
        var random = new Random();
        char[] resultChars = new char[length];

        for (int i = 0; i < resultChars.length; i++) {
            int n;

            do {
                n = random.nextInt(64);
            } while ((i == 0 || i == resultChars.length - 1) && n >= 62);

            if (n < 10) {
                resultChars[i] = (char) (48 + n); // 0-9
            } else if (n < 36) {
                resultChars[i] = (char) (65 + n - 10); // A-Z
            } else if (n < 62) {
                resultChars[i] = (char) (97 + n - 36); // a-z
            } else if (n == 62) {
                resultChars[i] = (char) (45); // -
            } else {
                resultChars[i] = (char) (95); // _
            }
        }

        return new String(resultChars);
    }

    public static String getRandomString(int length, long seed) {
        var random = new Random(seed);
        char[] resultChars = new char[length];

        for (int i = 0; i < resultChars.length; i++) {
            int n;

            do {
                n = random.nextInt(64);
            } while ((i == 0 || i == resultChars.length - 1) && n >= 62);

            if (n < 10) {
                resultChars[i] = (char) (48 + n); // 0-9
            } else if (n < 36) {
                resultChars[i] = (char) (65 + n - 10); // A-Z
            } else if (n < 62) {
                resultChars[i] = (char) (97 + n - 36); // a-z
            } else if (n == 62) {
                resultChars[i] = (char) (45); // -
            } else {
                resultChars[i] = (char) (95); // _
            }
        }

        return new String(resultChars);
    }
}
