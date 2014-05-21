/**
 * Copyright 2014 VU University Medical Center.
 * Licensed under the Apache License version 2.0 (see http://opensource.org/licenses/Apache-2.0).
 */

/**
 * File export functionality for the ODM-to-i2b2 tool. The ODM/XML file is converted in three tab-separated
 * files: the columns file, the wordmap file (these two contain the metadata) and the file with the actual
 * clinical data. These files can be loaded in the i2b2 database through, for instance, R-scripts.
 */
package nl.vumc.odmtoi2b2.export;
