package sk.dejw.android.georiddles.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.dejw.android.georiddles.models.Game;

public final class GameJsonUtils {

    public static Game[] getGamesFromJson(String jsonString) throws JSONException {

        /* String array to hold each day's weather String */
        Game[] games;

        JSONArray jsonArray = new JSONArray(jsonString);

        //TODO handle error

        games = new Game[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            /* Get the JSON object representing the day */
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Gson gson = new Gson();

            games[i] = gson.fromJson(jsonObject.toString(), Game.class);
        }
        return games;
    }
}