package sk.dejw.android.georiddles.utils.cursor;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.providers.GameContract;

public final class GameCursorUtils {

    public static ArrayList<Game> getGamesFromCursor(Cursor cursor) {
        final Integer GAME_ID = cursor.getColumnIndex(GameContract.Entry._ID);
        final Integer GAME_UUID = cursor.getColumnIndex(GameContract.Entry.COLUMN_UUID);
        final Integer GAME_TITLE = cursor.getColumnIndex(GameContract.Entry.COLUMN_TITLE);
        final Integer GAME_CODE = cursor.getColumnIndex(GameContract.Entry.COLUMN_CODE);
        final Integer GAME_GPS_LAT = cursor.getColumnIndex(GameContract.Entry.COLUMN_GPS_LAT);
        final Integer GAME_GPS_LNG = cursor.getColumnIndex(GameContract.Entry.COLUMN_GPS_LNG);

        ArrayList<Game> list = new ArrayList<>();
        if (cursor.getPosition() != -1) {
            cursor.moveToPosition(-1);
        }
        while (cursor.moveToNext()) {
            Game game = new Game(
                    cursor.getLong(GAME_ID),
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(GAME_TITLE),
                    cursor.getString(GAME_CODE),
                    cursor.getDouble(GAME_GPS_LAT),
                    cursor.getDouble(GAME_GPS_LNG));
            list.add(game);
        }
        return list;
    }

    public static Game getFirstGameFromCursor(Cursor cursor) {
        final Integer GAME_ID = cursor.getColumnIndex(GameContract.Entry._ID);
        final Integer GAME_UUID = cursor.getColumnIndex(GameContract.Entry.COLUMN_UUID);
        final Integer GAME_TITLE = cursor.getColumnIndex(GameContract.Entry.COLUMN_TITLE);
        final Integer GAME_CODE = cursor.getColumnIndex(GameContract.Entry.COLUMN_CODE);
        final Integer GAME_GPS_LAT = cursor.getColumnIndex(GameContract.Entry.COLUMN_GPS_LAT);
        final Integer GAME_GPS_LNG = cursor.getColumnIndex(GameContract.Entry.COLUMN_GPS_LNG);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Game game = new Game(
                    cursor.getLong(GAME_ID),
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(GAME_TITLE),
                    cursor.getString(GAME_CODE),
                    cursor.getDouble(GAME_GPS_LAT),
                    cursor.getDouble(GAME_GPS_LNG));
            return game;
        } else {
            return null;
        }
    }
}