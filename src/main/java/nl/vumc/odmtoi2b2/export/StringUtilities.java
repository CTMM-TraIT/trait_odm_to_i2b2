package nl.vumc.odmtoi2b2.export;

/**
 * This class assembles methods with operations on strings with regard to special characters.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class StringUtilities {

    /**
     * Since this is a utilities class, only the static methods are used and no instances are
     * ever created for this class. Therefore a private constructor is added to disable this
     * possibility.
     */
    private StringUtilities() {
    }

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
            final String twoDoubleQuotes = "\"\"";
            final String oneDoubleQuote = "\"";
            final String tick = "`";
            final String singleQuote = "'";
            final String backSlash = "\\\\";
            final String forwardSlash = "/";
            result = result.replaceAll(tick, singleQuote).replaceAll(backSlash, forwardSlash);
            while (result.contains(twoDoubleQuotes)) {
                result = result.replaceAll(twoDoubleQuotes, oneDoubleQuote);
            }
        }

        return result;
    }

    /**
     * Remove overabundant SEPARATOR symbols.
     *
     * @param text The string that is separated by a certain separator in a series of substrings. The separator may occur
     *             subsequently more than once, which is not desired.
     * @param separator The separator.
     * @param separatorInRegex The separator plus escape characters that symbolizes the separator in a regular
     *                         expression together.
     * @return The string in which the separator occurs subsequently maximally once.
     */
    public static String removeOverabundantSeparators(final String text, final String separator, final String separatorInRegex) {
        String result = text;

        while (result.contains(separator + separator)) {
            result = result.replaceAll(separatorInRegex + separatorInRegex, separator);
        }
        if (result.startsWith(separator)) {
            result = result.substring(1);
        }
        if (result.endsWith(separator)) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }
}
