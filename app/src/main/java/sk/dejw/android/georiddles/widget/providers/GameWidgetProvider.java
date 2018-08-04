package sk.dejw.android.georiddles.widget.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.GameContract;
import sk.dejw.android.georiddles.ui.GameActivity;
import sk.dejw.android.georiddles.ui.MainActivity;
import sk.dejw.android.georiddles.ui.RiddleActivity;
import sk.dejw.android.georiddles.ui.RiddleFragment;
import sk.dejw.android.georiddles.widget.services.GameService;

/**
 * Implementation of App Widget functionality.
 */
public class GameWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Game game, Riddle activeRiddle, int  riddlesTotalCount, int riddlesSolvedCount) {
        RemoteViews views = getGameRemoteView(context, game, activeRiddle, riddlesTotalCount, riddlesSolvedCount);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        GameService.startActionUpdateRecipeWidgets(context, GameContract.INVALID_ID);
    }

    public static void updateGameWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Game game, Riddle activeRiddle, int  riddlesTotalCount, int riddlesSolvedCount) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, game, activeRiddle, riddlesTotalCount, riddlesSolvedCount);
        }
    }

    private static RemoteViews getGameRemoteView(Context context, Game game, Riddle activeRiddle, int  riddlesTotalCount, int riddlesSolvedCount) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.game_widget);

        if(game == null) {
            views.setTextViewText(R.id.tv_widget_game_title, context.getString(R.string.widget_empty_view_text));
            views.setViewVisibility(R.id.tv_widget_game_statistics, View.GONE);
            views.setViewVisibility(R.id.ll_widget_riddle, View.GONE);
        } else {
            views.setTextViewText(R.id.tv_widget_game_title, game.getTitle());
            String statistics = context.getString(R.string.widget_solved) + " " +
                    String.valueOf(riddlesSolvedCount) + " " +
                    context.getString(R.string.widget_slash) + " " +
                    String.valueOf(riddlesTotalCount);
            views.setTextViewText(R.id.tv_widget_game_statistics, statistics);
            if(activeRiddle != null) {
                views.setTextViewText(R.id.tv_widget_active_riddle_title, activeRiddle.getTitle());
            }
        }

        Intent gameIntent;
        if (game == null) {
            gameIntent = new Intent(context, MainActivity.class);
        } else {
            gameIntent = new Intent(context, GameActivity.class);
            gameIntent.putExtra(GameActivity.BUNDLE_GAME, game);
        }
        PendingIntent gamePendingIntent = PendingIntent.getActivity(context, 0, gameIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_game_title, gamePendingIntent);

        if(activeRiddle != null) {
            Intent riddleIntent = new Intent(context, RiddleActivity.class);
            riddleIntent.putExtra(RiddleFragment.BUNDLE_RIDDLE, activeRiddle);
            PendingIntent riddlePendingIntent = PendingIntent.getActivity(context, 0, riddleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.tv_widget_active_riddle_title, riddlePendingIntent);
        }
        return views;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

