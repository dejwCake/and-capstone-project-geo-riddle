package sk.dejw.android.georiddles.utils.json;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.dejw.android.georiddles.models.Game;

public final class GameJsonUtils {
    private static final String TAG = GameJsonUtils.class.getSimpleName();

    public static Game[] getGamesFromJson(String jsonString) throws JSONException {

        //TODO change to array list
        Game[] games;

        JSONArray jsonArray = new JSONArray(jsonString);

        //TODO handle error

        games = new Game[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Gson gson = new Gson();

            games[i] = gson.fromJson(jsonObject.toString(), Game.class);
        }
        return games;
    }
}