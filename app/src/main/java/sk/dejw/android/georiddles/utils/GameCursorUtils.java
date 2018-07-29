package sk.dejw.android.georiddles.utils;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.provider.GameContract;

public final class GameCursorUtils {

    public static ArrayList<Game> getGamesFromCursor(Cursor cursor) {
        final Integer GAME_UUID = cursor.getColumnIndex(GameContract.COLUMN_UUID);
        final Integer GAME_TITLE = cursor.getColumnIndex(GameContract.COLUMN_TITLE);
        final Integer GAME_CODE = cursor.getColumnIndex(GameContract.COLUMN_CODE);

        ArrayList<Game> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Game game = new Game(
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(GAME_TITLE),
                    cursor.getString(GAME_CODE)
            );
            list.add(game);
        }
        return list;
    }

    public static Game getFirstGameFromCursor(Cursor cursor) {
        final Integer GAME_UUID = cursor.getColumnIndex(GameContract.COLUMN_UUID);
        final Integer GAME_TITLE = cursor.getColumnIndex(GameContract.COLUMN_TITLE);
        final Integer GAME_CODE = cursor.getColumnIndex(GameContract.COLUMN_CODE);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            Game game = new Game(
                    UUID.fromString(cursor.getString(GAME_UUID)),
                    cursor.getString(GAME_TITLE),
                    cursor.getString(GAME_CODE)
            );
            return game;
        } else {
            return null;
        }
    }
}