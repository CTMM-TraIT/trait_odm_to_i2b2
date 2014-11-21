package nl.vumc.odmtoi2b2.export;

import java.io.IOException;

/**
 * File exporter interface that sets the template for both the export to i2b2-full and i2b2-light.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public interface FileExporter {
    /**
     * Write the columns file: first the clinical data file name, then the path as specified in the second column of the
     * user's input concept map without the last node, then the column number and then the last node of the path.
     *
     * @param eventName         The human readable name of the event.
     * @param formName          The human readable name of the form (the CRF).
     * @param itemGroupName     The (most) human readable name of the item group.
     * @param preferredItemName The human readable name of the last node in the concept tree.
     * @param oidPath           The full path of OIDs, which provides a unique identifier for the columns.
     * @throws java.io.IOException An input-output exception.
     */
    void storeColumn(final String eventName,
                     final String formName,
                     final String itemGroupName,
                     final String preferredItemName,
                     final String oidPath)
            throws IOException;

    /**
     * Write the word mapping file: first the clinical data file name, then the column number, then the data value,
     * and then the mapped word.
     *
     * @param wordValue The possible values for those columns for which the values are mapped to a number.
     * @throws IOException An input-output exception.
     */
    void storeWord(final String wordValue) throws IOException;

    /**
     * Write the clinical data to a clinical data map, which is kept in the memory until the moment
     * that everything can be written out to a file writer in once.
     *
     * @param columnId           The full path of OIDs, which identifies a column.
     * @param dataValue          The value, which might not yet be converted to a number.
     * @param patientId          The identifier of the patient.
     * @param eventId            The OID of a type of repeating event.
     * @param eventRepeatKey     The repeat key that identifies an event repeat.
     * @param itemGroupId        The OID of a type of repeating item group.
     * @param itemGroupRepeatKey The repeat key that identifies an item group repeat.
     */
    void storeClinicalDataInfo(final String columnId,
                               final String dataValue,
                               final String patientId,
                               final String eventId,
                               final String eventRepeatKey,
                               final String itemGroupId,
                               final String itemGroupRepeatKey);

    /**
     * Close the export files.
     */
    void close();
}
