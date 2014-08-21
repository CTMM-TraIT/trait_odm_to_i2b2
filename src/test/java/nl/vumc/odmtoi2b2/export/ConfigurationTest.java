/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
 */

package nl.vumc.odmtoi2b2.export;

import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains unit tests for the Configuration class.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ConfigurationTest {
  /**
   * The resources directory for the export package.
   */
  private static final String EXPORT_DIRECTORY = Paths.get(
      "src", "test", "resources", "nl", "vumc", "odmtoi2b2", "export"
  ) + File.separator;

  /**
   * Test the constructor and the getters with an empty configuration file.
   */
  @Test(expected = NumberFormatException.class)
  public void testEmptyConfiguration() {
    final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "empty-configuration.properties");
    assertEquals(0, configuration.getMaxClinicalDataEntry());
    assertEquals("", configuration.getForbiddenSymbolRegex());
    assertFalse(configuration.getAvoidTransmartSymbolBugs());
  }

  /**
   * Test the constructor and the getters with a filled configuration file.
   */
  @Test
  public void testFilledConfiguration() {
    final Configuration configuration = new Configuration(EXPORT_DIRECTORY + "filled-configuration.properties");
    // Nice opportunity to use the fifth perfect number (see https://en.wikipedia.org/wiki/Perfect_number)... ;-)
    assertEquals(33550336, configuration.getMaxClinicalDataEntry());
    assertEquals("\t", configuration.getForbiddenSymbolRegex());
    assertTrue(configuration.getAvoidTransmartSymbolBugs());
  }
}
