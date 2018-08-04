package sk.dejw.android.georiddles.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.providers.GameContract;
import sk.dejw.android.georiddles.utils.cursor.GameCursorUtils;

public class GeoRiddlesState {

    public static void saveLastSelectedGame(Context context, long gameId) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.preference_key_game), gameId);
        editor.apply();
    }

    public static Game getLastSelectedGame(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences), Context.MODE_PRIVATE);
        long gameId = sharedPref.getLong(context.getString(R.string.preference_key_game), (int) GameContract.Entry.INVALID_ID);

        if (gameId == GameContract.Entry.INVALID_ID) {
            return null;
        }

        Cursor cursor = context.getContentResolver().query(
                GameContract.Entry.withId(gameId),
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.getCount() == 1) {
            return GameCursorUtils.getFirstGameFromCursor(cursor);
        } else {
            return null;
        }
    }

    public static void clearLastSelectedGame(Context context) {
        GeoRiddlesState.saveLastSelectedGame(context, GameContract.Entry.INVALID_ID);
    }
}
