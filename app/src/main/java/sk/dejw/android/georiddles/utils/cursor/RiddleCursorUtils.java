package sk.dejw.android.georiddles.utils.cursor;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.RiddleContract;

public final class RiddleCursorUtils {

    public static ArrayList<Riddle> getRiddlesFromCursor(Cursor cursor) {
        final Integer ID = cursor.getColumnIndex(RiddleContract._ID);
        final Integer GAME_UUID = cursor.getColumnIndex(RiddleContract.COLUMN_GAME_UUID);
        final Integer TITLE = cursor.getColumnIndex(RiddleContract.COLUMN_TITLE);
        final Integer NO = cursor.getColumnIndex(RiddleContract.COLUMN_NO);
        final Integer GPS_LAT = cursor.getColumnIndex(RiddleContract.COLUMN_GPS_LAT);
        final Integer GPS_LNG = cursor.getColumnIndex(RiddleContract.COLUMN_GPS_LNG);
        final Integer IMAGE_PATH = cursor.getColumnIndex(RiddleContract.COLUMN_IMAGE_PATH);
        final Integer QUESTION = cursor.getColumnIndex(RiddleContract.COLUMN_QUESTION);
        final Integer ANSWER = cursor.getColumnIndex(RiddleContract.COLUMN_ANSWER);
        final Integer ACTIVE = cursor.getColumnIndex(RiddleContract.COLUMN_ACTIVE);
        final Integer LOCATION_CHECKED = cursor.getColumnIndex(RiddleContract.COLUMN_LOCATION_CHECKED);
        final Integer RIDDLE_SOLVED = cursor.getColumnIndex(RiddleContract.COLUMN_RIDDLE_SOLVED);

        ArrayList<Riddle> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Riddle riddle = new Riddle(
                    cursor.getInt(ID),
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(TITLE),
                    cursor.getInt(NO),
                    cursor.getDouble(GPS_LAT),
                    cursor.getDouble(GPS_LNG),
                    cursor.getString(IMAGE_PATH),
                    cursor.getString(QUESTION),
                    cursor.getString(ANSWER),
                    cursor.getInt(ACTIVE) != 0,
                    cursor.getInt(LOCATION_CHECKED) != 0,
                    cursor.getInt(RIDDLE_SOLVED) != 0
            );
            list.add(riddle);
        }
        return list;
    }

    public static Riddle getFirstRiddleFromCursor(Cursor cursor) {
        final Integer ID = cursor.getColumnIndex(RiddleContract._ID);
        final Integer GAME_UUID = cursor.getColumnIndex(RiddleContract.COLUMN_GAME_UUID);
        final Integer TITLE = cursor.getColumnIndex(RiddleContract.COLUMN_TITLE);
        final Integer NO = cursor.getColumnIndex(RiddleContract.COLUMN_NO);
        final Integer GPS_LAT = cursor.getColumnIndex(RiddleContract.COLUMN_GPS_LAT);
        final Integer GPS_LNG = cursor.getColumnIndex(RiddleContract.COLUMN_GPS_LNG);
        final Integer IMAGE_PATH = cursor.getColumnIndex(RiddleContract.COLUMN_IMAGE_PATH);
        final Integer QUESTION = cursor.getColumnIndex(RiddleContract.COLUMN_QUESTION);
        final Integer ANSWER = cursor.getColumnIndex(RiddleContract.COLUMN_ANSWER);
        final Integer ACTIVE = cursor.getColumnIndex(RiddleContract.COLUMN_ACTIVE);
        final Integer LOCATION_CHECKED = cursor.getColumnIndex(RiddleContract.COLUMN_LOCATION_CHECKED);
        final Integer RIDDLE_SOLVED = cursor.getColumnIndex(RiddleContract.COLUMN_RIDDLE_SOLVED);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Riddle riddle = new Riddle(
                    cursor.getInt(ID),
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(TITLE),
                    cursor.getInt(NO),
                    cursor.getDouble(GPS_LAT),
                    cursor.getDouble(GPS_LNG),
                    cursor.getString(IMAGE_PATH),
                    cursor.getString(QUESTION),
                    cursor.getString(ANSWER),
                    cursor.getInt(ACTIVE) != 0,
                    cursor.getInt(LOCATION_CHECKED) != 0,
                    cursor.getInt(RIDDLE_SOLVED) != 0
            );
            return riddle;
        } else {
            return null;
        }
    }
}