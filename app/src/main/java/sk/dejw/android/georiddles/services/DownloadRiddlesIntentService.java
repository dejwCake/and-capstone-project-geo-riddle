package sk.dejw.android.georiddles.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.RiddleContract;
import sk.dejw.android.georiddles.providers.RiddleProvider;
import sk.dejw.android.georiddles.utils.json.RiddleJsonUtils;
import sk.dejw.android.georiddles.utils.network.RiddleNetworkUtils;

public class DownloadRiddlesIntentService extends IntentService {

    private static final String TAG = DownloadRiddlesIntentService.class.getSimpleName();
    public static final String RECEIVER = "receiver";
    public static final String GAME = "game";

    public static final int DOWNLOAD_SUCCESS = 200;
    public static final int SAVE_SUCCESS = 201;
    public static final int DOWNLOAD_ERROR = 400;
    public static final int SAVE_ERROR = 401;

    private Game mGame;
    private ResultReceiver mReceiver;

    public DownloadRiddlesIntentService() {
        super(DownloadRiddlesIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mGame = intent.getParcelableExtra(GAME);
            mReceiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER);

            ArrayList<Riddle> riddles = downloadData();
            if(riddles != null && riddles.size() > 0) {
                saveData(riddles);
            }
        }
    }

    private ArrayList<Riddle> downloadData() {
        try {
            URL requestUrl = RiddleNetworkUtils.buildUrl(this, mGame);
            Log.d(TAG, "Request url is " + requestUrl.toString());

            String jsonResponse = RiddleNetworkUtils.getResponseFromHttpUrl(requestUrl);

            ArrayList<Riddle> riddles = RiddleJsonUtils.getRiddlesFromJson(jsonResponse);
            mReceiver.send(DOWNLOAD_SUCCESS, Bundle.EMPTY);
            return riddles;
        } catch (Exception e) {
            mReceiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
            e.printStackTrace();
            return null;
        }
    }

    private void saveData(ArrayList<Riddle> riddles) {
        try {
            String selection = RiddleContract.COLUMN_GAME_UUID + " = ?";
            String[] selectionArgs = {mGame.getUuid().toString()};
            this.getContentResolver().delete(RiddleProvider.Riddles.RIDDLES_URI, selection, selectionArgs);

            boolean isOneActiveRiddle = false;
            int smallestNo = Integer.MAX_VALUE;
            if (riddles.size() > 0) {
                for (Riddle riddle : riddles) {
                    if (riddle.isActive()) {
                        isOneActiveRiddle = true;
                    }
                    if (riddle.getNo() < smallestNo) {
                        smallestNo = riddle.getNo();
                    }
                    ContentValues newRiddle = new ContentValues();
                    newRiddle.put(RiddleContract.COLUMN_GAME_UUID, mGame.getUuid().toString());
                    newRiddle.put(RiddleContract.COLUMN_TITLE, riddle.getTitle());
                    newRiddle.put(RiddleContract.COLUMN_NO, riddle.getNo());
                    newRiddle.put(RiddleContract.COLUMN_GPS_LAT, riddle.getGpsLat());
                    newRiddle.put(RiddleContract.COLUMN_GPS_LNG, riddle.getGpsLng());
                    newRiddle.put(RiddleContract.COLUMN_IMAGE_PATH, riddle.getImagePath());
                    newRiddle.put(RiddleContract.COLUMN_QUESTION, riddle.getQuestion());
                    newRiddle.put(RiddleContract.COLUMN_ANSWER, riddle.getAnswer());
                    newRiddle.put(RiddleContract.COLUMN_ACTIVE, riddle.isActive());
                    newRiddle.put(RiddleContract.COLUMN_LOCATION_CHECKED, riddle.isLocationChecked());
                    newRiddle.put(RiddleContract.COLUMN_RIDDLE_SOLVED, riddle.isRiddleSolved());
                    this.getContentResolver().insert(RiddleProvider.Riddles.RIDDLES_URI, newRiddle);
                }
                if (!isOneActiveRiddle) {
                    ContentValues activeRiddle = new ContentValues();
                    activeRiddle.put(RiddleContract.COLUMN_ACTIVE, true);
                    String selectionActive = RiddleContract.COLUMN_NO + " = ?";
                    String[] selectionActiveArgs = {String.valueOf(0)};
                    if (smallestNo != 0 && smallestNo != Integer.MAX_VALUE) {
                        selectionActiveArgs[0] = String.valueOf(smallestNo);
                    }
                    this.getContentResolver().update(RiddleProvider.Riddles.RIDDLES_URI, activeRiddle, selectionActive, selectionActiveArgs);
                }
            }
            mReceiver.send(SAVE_SUCCESS, Bundle.EMPTY);
        } catch (Exception e) {
            mReceiver.send(SAVE_ERROR, Bundle.EMPTY);
            e.printStackTrace();
        }
    }
}
