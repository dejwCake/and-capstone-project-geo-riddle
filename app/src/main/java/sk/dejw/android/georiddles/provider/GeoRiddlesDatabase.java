package sk.dejw.android.georiddles.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to create a database with one
 * table for messages
 */

@Database(version = GeoRiddlesDatabase.VERSION)
public class GeoRiddlesDatabase {

    public static final int VERSION = 1;

    @Table(GameContract.class)
    public static final String GAMES = "games";

    @Table(RiddleContract.class)
    public static final String RIDDLES = "riddles";

}
