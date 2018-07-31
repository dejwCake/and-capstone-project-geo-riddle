package sk.dejw.android.georiddles.utils.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import sk.dejw.android.georiddles.models.Game;

public class RiddleNetworkUtils {

    private static final String TAG = RiddleNetworkUtils.class.getSimpleName();

    private static final String URL = "http://georiddles.dejw.sk/games";

    public static java.net.URL buildUrl(Context context, Game game) {
        Uri builtUri = Uri.parse(URL);
        builtUri = builtUri.buildUpon()
                .appendPath(game.getUuid().toString())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                Log.d(TAG, "Input found");
                return scanner.next();
            } else {
                Log.d(TAG, "No more input");
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}