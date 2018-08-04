package sk.dejw.android.georiddles.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class GameContract {

    public static final String CONTENT_AUTHORITY = "sk.dejw.android.georiddles.games";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "games";

    public static final class Entry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String TABLE_NAME = "games";

        public static final String _ID = "_id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_GPS_LAT = "gps_lat";
        public static final String COLUMN_GPS_LNG = "gps_lng";

        public static final long INVALID_ID = -1;

        public static Uri withId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}