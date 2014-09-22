package nl.vumc.odmtoi2b2.export;

/**
 * This class assembles methods with operations on strings with regard to special characters.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class StringUtilities {

    /**
     * This method replaces:
     * - all the `-characters by '-characters
     * - all the \-characters by /-characters
     * - all n occurrences of the "-character by exactly one occurrence of the "-character.
     * @param input The input string
     * @return result: the resulting string
     */
    public static String convertString(final String input) {
        String result = input;
        if (input != null) {
            result = result.replaceAll("`", "'").replaceAll("\\\\", "/");
            while (result.contains("\"\"")) {
                result = result.replaceAll("\"\"", "\"");
            }
        }
        return result;
    }
}
