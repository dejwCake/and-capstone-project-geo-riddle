package sk.dejw.android.georiddles.utils.json;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sk.dejw.android.georiddles.models.Riddle;

public final class RiddleJsonUtils {

    public static ArrayList<Riddle> getRiddlesFromJson(String jsonString) throws JSONException {

        ArrayList<Riddle> riddles = new ArrayList<Riddle>();

        JSONArray jsonArray = new JSONArray(jsonString);

        //TODO handle error

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Gson gson = new Gson();

            Riddle riddle = gson.fromJson(jsonObject.toString(), Riddle.class);
            riddles.add(riddle);
        }
        return riddles;
    }
}