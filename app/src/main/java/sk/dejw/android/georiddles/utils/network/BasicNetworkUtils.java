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

public abstract class BasicNetworkUtils {

    protected static final String TAG = BasicNetworkUtils.class.getSimpleName();

    protected static final String URL = "";

    public static URL buildUrl(Context context) {
        Uri builtUri = Uri.parse(URL);
        builtUri = builtUri.buildUpon()
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