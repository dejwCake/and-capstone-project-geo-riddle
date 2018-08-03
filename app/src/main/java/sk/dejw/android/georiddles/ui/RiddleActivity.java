package sk.dejw.android.georiddles.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.provider.GameContract;
import sk.dejw.android.georiddles.provider.GameProvider;
import sk.dejw.android.georiddles.provider.RiddleContract;
import sk.dejw.android.georiddles.provider.RiddleProvider;
import sk.dejw.android.georiddles.utils.cursor.GameCursorUtils;

public class RiddleActivity extends AppCompatActivity implements
        RiddleFragment.OnCorrectLocationListener,
        RiddleFragment.OnCorrectAnswerListener {
    private static final String TAG = RiddleActivity.class.getSimpleName();

    private Riddle mRiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle);

        if (savedInstanceState == null) {
            Intent startingIntent = getIntent();
            if (startingIntent != null) {
                Log.d(TAG, "Intent: " + startingIntent.toString());
                if (startingIntent.hasExtra(RiddleFragment.BUNDLE_RIDDLE)) {
                    Log.d(TAG, "Extras: " + startingIntent.getExtras().toString());
                    mRiddle = startingIntent.getExtras().getParcelable(RiddleFragment.BUNDLE_RIDDLE);
                }
            }
        } else {
            mRiddle = savedInstanceState.getParcelable(RiddleFragment.BUNDLE_RIDDLE);
        }

        Log.d(TAG, "Riddle: " + mRiddle.getTitle());
        setTitle(mRiddle.getTitle());

        setupFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelable(RiddleFragment.BUNDLE_RIDDLE, mRiddle);
        super.onSaveInstanceState(outState);
    }

    private void setupFragment() {
        Log.d(TAG, "setupFragment");

        RiddleFragment riddleFragment = RiddleFragment.newInstance(mRiddle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.riddle_container, riddleFragment)
                .commit();
    }

    @Override
    public void onCorrectLocation(Riddle riddle) {
        mRiddle.setLocationChecked(riddle.isLocationChecked());
    }

    @Override
    public void onCorrectAnswer(Riddle riddle, Riddle nextRiddle) {
        if(nextRiddle == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}
