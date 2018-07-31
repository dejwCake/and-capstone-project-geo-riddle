package sk.dejw.android.georiddles.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.utils.GameJsonUtils;
import sk.dejw.android.georiddles.utils.network.GameNetworkUtils;

public class FetchGamesTask extends AsyncTask<String, Void, Game[]> {
    private static final String TAG = FetchGamesTask.class.getSimpleName();

    private Context mContext;
    private AsyncTaskCompleteListener<Game[]> listener;

    public FetchGamesTask(Context context, AsyncTaskCompleteListener<Game[]> listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Game[] doInBackground(String... params) {
        URL requestUrl = GameNetworkUtils.buildUrl(mContext);
        Log.d(TAG, "Request url is " + requestUrl.toString());

        try {
            String jsonResponse = GameNetworkUtils.getResponseFromHttpUrl(requestUrl);

            return GameJsonUtils.getGamesFromJson(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Game[] games) {
        super.onPostExecute(games);
        listener.onTaskComplete(games);
    }
}
