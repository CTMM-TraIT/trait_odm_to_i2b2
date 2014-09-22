/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

// todo: replace "http://opensource.org/licenses/Apache-2.0" everywhere with "http://www.apache.org/licenses/LICENSE-2.0.html"
// with Ctrl-Shift-R (see https://github.com/CTMM-TraIT/trait_workflow_runner)

package nl.vumc.odmtoi2b2.export;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo: add Javadocs.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 */
public class Configuration {
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    /**
     * todo: add Javadocs.
     */
    private int maxClinicalDataEntry;

    /**
     * todo: add Javadocs.
     */
    private String forbiddenSymbolRegex;

    /**
     * todo: add Javadocs.
     */
    private boolean avoidTransmartSymbolBugs;

    /**
     * todo: add Javadocs.
     *
     * @param propertiesFilePath
     */
    public Configuration(final String propertiesFilePath) {
        try {
            final Properties properties = new Properties();
            properties.load(new FileReader(propertiesFilePath));

            final String maxClinicalDataEntryAsString = properties.getProperty("max-clinical-data-entry");
            this.maxClinicalDataEntry = Integer.parseInt(maxClinicalDataEntryAsString);
            this.forbiddenSymbolRegex = properties.getProperty("forbidden-symbols-regex");
            final String avoidTransmartSymbolBugsAsString = properties.getProperty("avoid-transmart-symbol-bugs");
            this.avoidTransmartSymbolBugs = Boolean.parseBoolean(avoidTransmartSymbolBugsAsString);

        } catch (final IOException e) {
            final String message = "Exception while reading configuration properties from file %s.";
            logger.error(String.format(message, propertiesFilePath), e);
        }
    }

    /**
     * todo: add Javadocs.
     *
     * @return
     */
    public int getMaxClinicalDataEntry() {
        return maxClinicalDataEntry;
    }

    /**
     * todo: add Javadocs.
     *
     * @return
     */
    public String getForbiddenSymbolRegex() {
        return forbiddenSymbolRegex;
    }

    /**
     * todo: add Javadocs.
     *
     * @return
     */
    public boolean getAvoidTransmartSymbolBugs() {
        return avoidTransmartSymbolBugs;
    }
}
