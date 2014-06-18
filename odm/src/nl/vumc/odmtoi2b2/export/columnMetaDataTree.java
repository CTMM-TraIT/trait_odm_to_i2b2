package nl.vumc.odmtoi2b2.export;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: PA-NB101
 * Date: 18-6-14
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */

/**
 * A ColumnMetaDataTree is a .
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class ColumnMetaDataTree {

    /**
     * The path consisting of OIDs of study - event - form - itemgroup - item + an optional event
     * repeat suffix in case of columns that refer to repeating itemGroups that are themselves
     * part of a repeating event. In case of no repeats at all, items correspond to columns. In
     * this case the partial column IDs list contains only a single element and the ID is
     * actually complete.
     */
    private List <String> partialColumnIds;

    /**
     *  todo: fill in
     */
    private List <ColumnMetaDataTree> columnMetaDataTrees;

}
