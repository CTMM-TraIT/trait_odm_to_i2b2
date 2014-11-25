/**
 * Copyright(c) 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

// todo: replace "http://opensource.org/licenses/Apache-2.0" everywhere with "http://www.apache.org/licenses/LICENSE-2.0.html"
// with Ctrl-Shift-R (see https://github.com/CTMM-TraIT/trait_workflow_runner)

package nl.vumc.odmtoi2b2.export;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class queries the configuration file. This configuration file can be modified by the user
 * to set values for the maximal length of a text field in the clinical data file, or to indicate
 * which symbols might be forbidden.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 */
public class Configuration {
    /**
     * The location of the log4j properties file.
     */
    private String log4jPropertiesPath;

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    /**
     * The maximal length for a text field in the clinical data file (e.g. 256).
     */
    private int maxClinicalDataEntry;

    /**
     * A regular expression that indicates which symbols are forbidden in the export to the database.
     */
    private String forbiddenSymbolRegex;

    /**
     * A boolean that indicates whether the export should be adapted to avoid specific bugs in the
     * tranSMART data analysis platform.
     */
    private boolean avoidTransmartSymbolBugs;

    /**
     * Construct the configuration object by reading in the properties in the configuration file.
     *
     * @param propertiesFilePath The path that identifies the configuration file.
     */
    public Configuration(final String propertiesFilePath) {
        try {
            final Properties properties = new Properties();
            final FileInputStream fileInputStream = new FileInputStream(propertiesFilePath);
            final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            properties.load(inputStreamReader);

            this.log4jPropertiesPath = properties.getProperty("log4j-properties-file");
            final String maxClinicalDataEntryAsString = properties.getProperty("max-clinical-data-entry");
            this.maxClinicalDataEntry = Integer.parseInt(maxClinicalDataEntryAsString);
            this.forbiddenSymbolRegex = properties.getProperty("forbidden-symbols-regex");
            final String avoidTransmartSymbolBugsAsString = properties.getProperty("avoid-transmart-symbol-bugs");
            this.avoidTransmartSymbolBugs = Boolean.parseBoolean(avoidTransmartSymbolBugsAsString);

            fileInputStream.close();
            inputStreamReader.close();
        } catch (final IOException e) {
            final String message = "Exception while reading configuration properties from file %s.";
            logger.error(String.format(message, propertiesFilePath), e);
        }
    }

    /**
     * Get the path and the filename that contain the properties for logging errors, warnings, etc.,
     * called the log4j properties file.
     *
     * @return the path to the log4j file.
     */
    public String getLog4jPath() {
        return log4jPropertiesPath;
    }

    /**
     * Get the maximal-length-property for a text field in the clinical data file from the configuration file.
     *
     * @return the maximal length.
     */
    public int getMaxClinicalDataEntry() {
        return maxClinicalDataEntry;
    }

    /**
     * Get the regular expression that contains the list of symbols that are forbidden in the export.
     *
     * @return the regular expression that contains the list of symbols that are forbidden in the export.
     */
    public String getForbiddenSymbolRegex() {
        return forbiddenSymbolRegex;
    }

    /**
     * Get the information whether special measures should be taken to avoid specific bugs in the
     * tranSMART system. True = take special measures.
     *
     * @return the boolean that says true in case special measures have to be taken to avoid tranSMART bugs.
     */
    public boolean getAvoidTransmartSymbolBugs() {
        return avoidTransmartSymbolBugs;
    }
}
