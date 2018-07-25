package sk.dejw.android.georiddles.asyncTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.provider.GameContract;
import sk.dejw.android.georiddles.provider.GameProvider;

public class SaveGamesTask extends AsyncTask<Game[], Void, String> {
    private static final String TAG = SaveGamesTask.class.getSimpleName();

    public static final String OK_RESULT = "OK";

    private Context mContext;
    private AsyncTaskCompleteListener<String> listener;

    public SaveGamesTask(Context context, AsyncTaskCompleteListener<String> listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Game[]... params) {
        Game[] games = params[0];
        try {

            Cursor cursor = mContext.getContentResolver().query(
                    GameProvider.Games.GAMES_URI,
                    null,
                    null,
                    null,
                    GameContract.COLUMN_UUID
            );
//
            for (int i = 0; i < games.length; i++) {
                ContentValues newGame = new ContentValues();
                newGame.put(GameContract.COLUMN_UUID, games[i].getUuid().toString());
                newGame.put(GameContract.COLUMN_TITLE, games[i].getTitle());
                newGame.put(GameContract.COLUMN_CODE, games[i].getCode());
                if (mContext.getContentResolver().update(GameProvider.Games.GAMES_URI, newGame, GameContract.COLUMN_UUID + " = ?", new String[]{games[i].getUuid().toString()}) == 0) {
                    mContext.getContentResolver().insert(GameProvider.Games.GAMES_URI, newGame);
                }
            }

            return OK_RESULT;

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }
}
