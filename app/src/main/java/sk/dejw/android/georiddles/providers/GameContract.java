package sk.dejw.android.georiddles.providers;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) library to define the columns in a
 * content provider baked by a database
 */

public class GameContract {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_UUID = "uuid";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_TITLE = "title";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_CODE = "code";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String COLUMN_GPS_LAT = "gps_lat";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String COLUMN_GPS_LNG = "gps_lng";

    public static final long INVALID_ID = -1;
}