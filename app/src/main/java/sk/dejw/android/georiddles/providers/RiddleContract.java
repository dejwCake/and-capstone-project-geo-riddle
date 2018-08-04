package sk.dejw.android.georiddles.providers;

import android.net.Uri;
import android.provider.BaseColumns;


public class RiddleContract {

    public static final String CONTENT_AUTHORITY = "sk.dejw.android.georiddles";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "riddles";

    public static final class Entry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String TABLE_NAME = "riddles";

        public static final String _ID = "_id";
        public static final String COLUMN_GAME_UUID = "game_uuid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NO = "no";
        public static final String COLUMN_GPS_LAT = "gps_lat";
        public static final String COLUMN_GPS_LNG = "gps_lng";
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_LOCATION_CHECKED = "location_checked";
        public static final String COLUMN_RIDDLE_SOLVED = "riddle_solved";

        public static Uri withId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}