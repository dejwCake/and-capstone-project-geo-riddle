package sk.dejw.android.georiddles.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String BUNDLE_GAME = "game";

    Game mGame = null;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            Log.d(TAG, "Intent: " + startingIntent.toString());
            if (startingIntent.hasExtra(BUNDLE_GAME)) {
                Log.d(TAG, "Extras: " + startingIntent.getExtras().toString());
                mGame = startingIntent.getExtras().getParcelable(BUNDLE_GAME);
            }
        }

        if (mGame == null && savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_GAME)) {
            Log.d(TAG, "savedInstanceState: " + savedInstanceState.toString());
            mGame = savedInstanceState.getParcelable(BUNDLE_GAME);
        }
        Log.d(TAG, "Game: " + mGame.getTitle());

        setTitle(mGame.getTitle());
    }
}
