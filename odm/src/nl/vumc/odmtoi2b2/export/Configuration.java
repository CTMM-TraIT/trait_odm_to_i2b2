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

/**
 * todo
 */
public class Configuration {
    private int maxClinicalDataEntry;

    public Configuration(final String propertiesFilePath) {
        try {
            final Properties properties = new Properties();
            properties.load(new FileReader(propertiesFilePath));

            final String minutesString = properties.getProperty("max-clinical-data-entry");
            this.maxClinicalDataEntry = Integer.parseInt(minutesString);
        } catch (final IOException e) {
            // todo: logger.
            e.printStackTrace();
        }
    }

    public int getMaxClinicalDataEntry() {
        return maxClinicalDataEntry;
    }
}
