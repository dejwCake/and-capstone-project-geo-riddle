package sk.dejw.android.georiddles.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleActivity extends AppCompatActivity {
    private static final String TAG = RiddleActivity.class.getSimpleName();

    private Riddle mRiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            RiddleFragment riddleFragment = new RiddleFragment();
            riddleFragment.setRiddle(mRiddle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.riddle_container, riddleFragment)
                    .commit();
        } else {
            mRiddle = savedInstanceState.getParcelable(RiddleFragment.BUNDLE_RIDDLE);
        }

        Log.d(TAG, "Riddle: " + mRiddle.getTitle());
        setTitle(mRiddle.getTitle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RiddleFragment.BUNDLE_RIDDLE, mRiddle);
        super.onSaveInstanceState(outState);
    }
}
