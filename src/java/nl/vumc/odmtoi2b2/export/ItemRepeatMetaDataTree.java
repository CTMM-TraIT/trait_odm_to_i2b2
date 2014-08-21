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
 * An ItemRepeatMetaDataTree is a metadata structure that represents a list of repeated item values.
 * Items can be repeated both in different events as in different item groups. The metadata
 * structure consists of a set of statistical data about the list, like average value, last value,
 * maximum value, minimum value, array of all the values, value of repeat 01, repeat 02,...,
 * number of repeats, number of repeats in repeats, etc.
 *
 * Example:
 * item: temperature
 * partial column id: event1/CRF2/itemGroup3/temperature
 * itemRepeatMetaDataTrees: <event repeat 01, event repeat 02, event repeat 03>
 *
 * column1 : array: [[36, 37], [38], [38, 39]]
 * column2 : average: 37.6
 * column3 : number of event repeats: 3
 * column4 : number of repeats: 5
 * column5 : maximum value: 39
 * column6 : minimum value: 36
 * column7 : maximum event value: 38.5
 * column8 : minimum event value: 36.5
 *
 *
 * event repeat 01: another itemRepeatMetaDataTree
 * partial column id: event1/CRF2/itemGroup3/temperature/event_repeat_01
 * itemRepeatMetaDataTrees: null
 *
 * column 9 : array: [36,37]
 * column 10: average: 36.5
 * column 11: number of repeats: 2
 * column 12: maximum value: 37
 * column 13: minimum value: 36
 *
 * event repeat 02: another itemRepeatMetaDataTree
 * partial column id: event1/CRF2/itemGroup3/temperature/event_repeat_02
 * itemRepeatMetaDataTrees: null
 *
 * column 14: array: [38]
 * column 15: average: 38
 * column 16: number of repeats: 1
 * column 17: maximum value: 38
 * column 18: minimum value: 38
 *
 * event repeat 03: another itemRepeatMetaDataTree (see below)
 * partial column id: event1/CRF2/itemGroup3/temperature/event_repeat_03
 * itemRepeatMetaDataTrees: null
 *
 * column 19: array: [38, 39]
 * column 20: average: 38.5
 * column 21: number of repeats: 2
 * column 22: maximum value: 39
 * column 23: minimum value: 38
 *
 *
 * @author <a href="mailto:w.blonde@vumc.nl">Ward Blondé</a>
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */

public class ItemRepeatMetaDataTree {

    /**
     * The path consisting of OIDs of study - event - form - itemgroup - item + an optional event
     * repeat suffix in case of columns that refer to repeating itemGroups that are themselves
     * part of a repeating event. In case of no repeats at all, items correspond to columns. In
     * this case the partial column IDs list contains only a single element and the ID is
     * actually complete.
     */
    private List <String> partialColumnIds;

    /**
     *  This is a list of metadatastructures, each of which represents a little item value
     *  repeats list.
     */
    private List <ItemRepeatMetaDataTree> itemRepeatMetaDataTrees;

}
