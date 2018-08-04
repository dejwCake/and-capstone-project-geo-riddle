package sk.dejw.android.georiddles.utils.json;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import sk.dejw.android.georiddles.models.Riddle;

public final class RiddleJsonUtils {

    public static ArrayList<Riddle> getRiddlesFromJson(String jsonString) throws JSONException {

        final String GAME_UUID = "uuid";
        final String GAME_RIDDLES = "riddles";

        final String RIDDLE_TITLE = "title";
        final String RIDDLE_NO = "riddle_no";
        final String RIDDLE_GPS_LAT = "gps_lat";
        final String RIDDLE_GPS_LNG = "gps_lng";
        final String RIDDLE_IMAGE_PATH = "image_path";
        final String RIDDLE_QUESTION = "question";
        final String RIDDLE_ANSWER = "answer";

        ArrayList<Riddle> riddles = new ArrayList<Riddle>();

        JSONObject jsonGameObject = new JSONObject(jsonString);
        UUID gameUuid = UUID.fromString(jsonGameObject.getString(GAME_UUID));
        JSONArray jsonArray = jsonGameObject.getJSONArray(GAME_RIDDLES);

        //TODO handle error

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Gson gson = new Gson();

            Riddle riddle = new Riddle(
                    gameUuid,
                    jsonObject.getString(RIDDLE_TITLE),
                    jsonObject.getInt(RIDDLE_NO),
                    jsonObject.getDouble(RIDDLE_GPS_LAT),
                    jsonObject.getDouble(RIDDLE_GPS_LNG),
                    jsonObject.getString(RIDDLE_IMAGE_PATH),
                    jsonObject.getString(RIDDLE_QUESTION),
                    jsonObject.getString(RIDDLE_ANSWER)
            );
            ;
            riddles.add(riddle);
        }
        return riddles;
    }
}