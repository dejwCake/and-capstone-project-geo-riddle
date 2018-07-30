package sk.dejw.android.georiddles.provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to define the columns in a
 * content provider baked by a database
 */

public class RiddleContract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_GAME_UUID = "game_uuid";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_TITLE = "title";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_NO = "no";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String COLUMN_GPS_LAT = "gps_lat";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String COLUMN_GPS_LNG = "gps_lng";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_IMAGE_PATH = "image_path";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_QUESTION = "question";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_ANSWER = "answer";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_ACTIVE = "active";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_LOCATION_CHECKED = "location_checked";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_RIDDLE_SOLVED = "riddle_solved";
}