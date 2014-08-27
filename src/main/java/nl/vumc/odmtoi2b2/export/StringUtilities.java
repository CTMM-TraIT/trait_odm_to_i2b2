package nl.vumc.odmtoi2b2.export;

/**
 * Created with IntelliJ IDEA.
 * User: PA-NB101
 * Date: 27-8-14
 * Time: 17:22
 */
public class StringUtilities {
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
