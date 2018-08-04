package sk.dejw.android.georiddles.widget.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.GameContract;
import sk.dejw.android.georiddles.providers.RiddleContract;
import sk.dejw.android.georiddles.utils.GeoRiddlesState;
import sk.dejw.android.georiddles.utils.cursor.GameCursorUtils;
import sk.dejw.android.georiddles.utils.cursor.RiddleCursorUtils;
import sk.dejw.android.georiddles.widget.providers.GameWidgetProvider;

public class GameService extends IntentService {
    public static final String TAG = GameService.class.getSimpleName();

    public static final String ACTION_UPDATE_GAME_WIDGETS = "sk.dejw.android.georiddles.action.update_game_widgets";
    public static final String EXTRA_GAME_ID = "sk.dejw.android.georiddles.extra.game_id";

    public GameService() {
        super("GameService");
    }

    public static void startActionUpdateRecipeWidgets(Context context, long gameId) {
        Intent intent = new Intent(context, GameService.class);
        intent.setAction(ACTION_UPDATE_GAME_WIDGETS);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_GAME_WIDGETS.equals(action)) {
                final long gameId = intent.getLongExtra(EXTRA_GAME_ID, GameContract.Entry.INVALID_ID);
                handleActionUpdateGameWidgets(gameId);
            }
        }
    }

    private void handleActionUpdateGameWidgets(long gameId) {
        Cursor cursor = null;
        Game game = null;
        Riddle activeRiddle = null;
        int riddlesTotalCount = 0;
        int riddlesSolvedCount = 0;
        if (gameId == GameContract.Entry.INVALID_ID) {
            game = GeoRiddlesState.getLastSelectedGame(this);
        } else {
            cursor = getContentResolver().query(
                    GameContract.Entry.withId(gameId),
                    null,
                    null,
                    null,
                    GameContract.Entry.COLUMN_TITLE
            );
            Log.d(TAG, "Cursor count: " + cursor.getCount());
            game = GameCursorUtils.getFirstGameFromCursor(cursor);
            cursor.close();
        }

        if (game != null) {
            cursor = getContentResolver().query(
                    RiddleContract.Entry.CONTENT_URI,
                    null,
                    null,
                    null,
                    RiddleContract.Entry.COLUMN_NO
            );
            ArrayList<Riddle> riddles = RiddleCursorUtils.getRiddlesFromCursor(cursor);
            cursor.close();

            riddlesTotalCount = riddles.size();
            for (Riddle riddle : riddles) {
                if (riddle.isActive() && activeRiddle == null) {
                    activeRiddle = riddle;
                }
                if (riddle.isRiddleSolved()) {
                    riddlesSolvedCount++;
                }
            }
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, GameWidgetProvider.class));
        GameWidgetProvider.updateGameWidgets(this, appWidgetManager, appWidgetIds, game, activeRiddle, riddlesTotalCount, riddlesSolvedCount);
    }
}
