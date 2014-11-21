package nl.vumc.odmtoi2b2.export;

import java.io.IOException;

/**
 * File exporter for the I2B2 "light" data model.
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class FileExporterLight implements FileExporter {
    @Override
    public void storeColumn(final String eventName, final String formName, final String itemGroupName,
                            final String preferredItemName, final String oidPath) throws IOException {
    }

    @Override
    public void storeWord(final String wordValue) throws IOException {
        System.out.println("wordValue = " + wordValue);
    }

    @Override
    public void storeClinicalDataInfo(final String columnId, final String dataValue, final String patientId,
                                      final String eventId, final String eventRepeatKey, final String itemGroupId,
                                      final String itemGroupRepeatKey) {
    }

    @Override
    public void close() {
        System.out.println("FileExporterLight.close");
    }
}
