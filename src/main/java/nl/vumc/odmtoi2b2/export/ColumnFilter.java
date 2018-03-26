package nl.vumc.odmtoi2b2.export;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
/**
 * Filter to remove Items from the conversion from ODM-files to I2B2 files.
 * The {@link ColumnFilter} is created from a tab delimited file which has to contain the complete ODM-axis. The
 * required columns are:
 * <ul>
 *     <li>StudyEvent_Name</li>
 *     <li>Form_Name</li>
 *     <li>ItemGroup_Name</li>
 *     <li>Item_Name</li>
 * </ul>
 *
 * The file must contain the items to <b>excluded</b>.
 *
 * @author <a href="mailto:j.rousseau@nki.nl">Jacob Rousseau</a>
 */
public class ColumnFilter {
    /**
     * The required column names in this specific order.
     */
    private static final List<String> REQUIRED_COLUMN_NAMES = new ArrayList<>();
    static {
        REQUIRED_COLUMN_NAMES.add("StudyEvent_Name");
        REQUIRED_COLUMN_NAMES.add("Form_Name");
        REQUIRED_COLUMN_NAMES.add("ItemGroup_Name");
        REQUIRED_COLUMN_NAMES.add("Item_Name");
    }

    /**
     * A list of the StudyEvent_Name, Form_Name, ItenGroup_Name and Item_Name to exclude.
     */
    private List<String> excludedItemList;

    /**
     * The constuctor.
     * @param filePath path to the file defining the excluded items.
     */
    public ColumnFilter(final String filePath) {
        excludedItemList = new ArrayList<>();
        if (StringUtils.isNotBlank(filePath)) {
            loadItemsFromFile(filePath);
        }
    }

    /**
     * Determines if a column is excluded.
     * @param oidPath the ODM path
     * @return <code>true</code> if the oidPath is to be included.
     */
    public boolean isIncluded(final String oidPath) {
        return !excludedItemList.contains(oidPath);
    }

    /**
     * Load a file from the specified path.
     * @param filePath the path to the file
     */
    private void loadItemsFromFile(final String filePath) {
        final List<String> lineList = new ArrayList<>();
        try {
            final BufferedReader buf = new BufferedReader(new FileReader(filePath));
            String line;

            while (true) {
                line = buf.readLine();
                if (line == null) {
                    break;
                } else {
                    lineList.add(line);
                }
            }
        }
        catch (final IOException ioe) {
            throw new IllegalStateException("Problem reading file: " + filePath + ".", ioe);
        }
        final String tabSeparator = "\t";
        final String columnHeader = lineList.get(0);
        final String columnNameList = Arrays.asList(columnHeader.split(tabSeparator)).toString();
        final String requiredColumns = REQUIRED_COLUMN_NAMES.toString();

        if (!requiredColumns.equals(columnNameList)) {
            throw new IllegalStateException("Missing column(s), wrong name or column order not correct. Columns "
                    + "names must be: " + requiredColumns);
        }

        lineList.remove(0);
        for (String line : lineList) {
            final String[] cellList = line.split(tabSeparator);
            final String value = String.join(OdmToFilesConverter.PLUS, cellList);
            excludedItemList.add(value);
        }
    }
}
