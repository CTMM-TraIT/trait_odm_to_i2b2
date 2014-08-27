package nl.vumc.odmtoi2b2.export;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: PA-NB101
 * Date: 27-8-14
 * Time: 17:24
 */
public class StringUtilitiesTest {
    @Test
    public void testConvertString() {
        assertNull(StringUtilities.convertString(null));
        assertEquals("'", StringUtilities.convertString("`"));
        assertEquals("\"", StringUtilities.convertString("\"\"\"\""));
    }
}
