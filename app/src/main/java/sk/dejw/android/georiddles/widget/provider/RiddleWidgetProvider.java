package sk.dejw.android.georiddles.widget.provider;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import sk.dejw.android.bakingrecipes.models.Recipe;
import sk.dejw.android.bakingrecipes.services.RecipeWidgetService;
import sk.dejw.android.bakingrecipes.ui.RecipeDetailActivity;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleWidgetProvider extends AppWidgetProvider {

    public static final String TAG = RiddleWidgetProvider.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Riddle riddle) {
        RemoteViews rv;
        rv = getRiddleRemoteView(context, riddle, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private static RemoteViews getRiddleRemoteView(Context context, Riddle riddle, int appWidgetId) {
        Log.d(TAG, "getRiddleRemoteView recipeId = " + riddle.getId());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_riddle);

//        Intent intent = new Intent(context, RecipeWidgetService.class);
//        views.setRemoteAdapter(R.id.gv_widget_recipe_ingredients_view, intent);

        views.setTextViewText(R.id.tv_widget_recipe_name, riddle.getName());
        views.setViewVisibility(R.id.tv_widget_recipe_name, View.VISIBLE);

        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gv_widget_recipe_ingredients_view, appPendingIntent);
        views.setEmptyView(R.id.gv_widget_recipe_ingredients_view, R.id.empty_view);
        return views;
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Recipe recipe) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        RecipeService.startActionUpdateRecipeWidgets(context, RecipeContract.INVALID_RECIPE_ID);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
//        RecipeService.startActionUpdateRecipeWidgets(context, RecipeContract.INVALID_RECIPE_ID);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

}
