package sk.dejw.android.georiddles.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.RiddleContract;
import sk.dejw.android.georiddles.services.DownloadRiddlesIntentService;
import sk.dejw.android.georiddles.utils.GeoRiddlesState;
import sk.dejw.android.georiddles.utils.cursor.RiddleCursorUtils;
import sk.dejw.android.georiddles.utils.network.GlobalNetworkUtils;
import sk.dejw.android.georiddles.widget.services.GameService;

public class GameActivity extends AppCompatActivity implements
        RiddleListFragment.OnRiddleClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        RiddleFragment.OnCorrectLocationListener,
        RiddleFragment.OnCorrectAnswerListener {

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String BUNDLE_GAME = "game";
    public static final String BUNDLE_RIDDLES = "riddles";

    private static final int RIDDLES_FIRST_ATTEMPT_LOADER_ID = 152;
    private static final int RIDDLES_SECOND_ATTEMPT_LOADER_ID = 153;

    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.game_main_content)
    LinearLayout mGameMainContent;

    private Game mGame = null;
    private ArrayList<Riddle> mRiddles;
    private Bundle mSavedInstanceState;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            Log.d(TAG, "Intent: " + startingIntent.toString());
            if (startingIntent.hasExtra(BUNDLE_GAME)) {
                Log.d(TAG, "Extras: " + startingIntent.getExtras().toString());
                mGame = startingIntent.getExtras().getParcelable(BUNDLE_GAME);
            }
        }

        if (savedInstanceState != null) {
            mGame = savedInstanceState.getParcelable(BUNDLE_GAME);
            mRiddles = savedInstanceState.getParcelableArrayList(BUNDLE_RIDDLES);
        }

        if (mGame == null) {
            mGame = GeoRiddlesState.getLastSelectedGame(this);
        }

        if (mGame != null) {
            GameService.startActionUpdateRecipeWidgets(this, mGame.getId());
        }

        Log.d(TAG, "Game: " + mGame.getTitle());
        setTitle(mGame.getTitle());

        if (mRiddles == null || mRiddles.size() == 0) {
            loadRiddlesFromDb(RIDDLES_FIRST_ATTEMPT_LOADER_ID);
        } else {
            setupFragments();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelable(BUNDLE_GAME, mGame);
        outState.putParcelableArrayList(BUNDLE_RIDDLES, mRiddles);
        super.onSaveInstanceState(outState);
    }

    private void setupFragments() {
        Log.d(TAG, "setupFragments");

        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (mSavedInstanceState == null) {
        RiddleListFragment riddleListFragment = RiddleListFragment.newInstance(mRiddles);
        fragmentManager.beginTransaction()
                .replace(R.id.game_container, riddleListFragment)
                .commit();
//        }

        if (findViewById(R.id.riddle_container) != null) {
            mTwoPane = true;

//            if (mSavedInstanceState == null) {

            Riddle activeRiddle = null;
            for (Riddle riddle : mRiddles) {
                if (riddle.isActive() && activeRiddle == null) {
                    activeRiddle = riddle;
                }
            }
            if (activeRiddle == null) {
                activeRiddle = mRiddles.get(0);
            }
            RiddleFragment riddleFragment = RiddleFragment.newInstance(activeRiddle);
            fragmentManager.beginTransaction()
                    .replace(R.id.riddle_container, riddleFragment)
                    .commit();
//            }
        } else {
            mTwoPane = false;
        }
    }

    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage");

        mGameMainContent.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        Log.d(TAG, "showDataView");

        mErrorMessage.setVisibility(View.INVISIBLE);
        mGameMainContent.setVisibility(View.VISIBLE);
    }

    private void loadRiddlesFromDb(int attempt) {
        Log.d(TAG, "loadRiddlesFromDb");

        mLoadingIndicator.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(attempt, null, this);
    }

    private void loadRiddlesFromInternet() {
        Log.d(TAG, "loadRiddlesFromInternet");

        if (GlobalNetworkUtils.hasConnection(this)) {
            Log.d(TAG, "Internet working.");

            Intent startIntent = new Intent(this, DownloadRiddlesIntentService.class);
            startIntent.putExtra(DownloadRiddlesIntentService.RECEIVER, new DownloadReceiver(new Handler()));
            startIntent.putExtra(DownloadRiddlesIntentService.GAME, mGame);
            startService(startIntent);

            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Internet not working.");

            loadRiddlesFromDb(RIDDLES_SECOND_ATTEMPT_LOADER_ID);
        }
    }

    @Override
    public void onRiddleSelected(long riddleId) {
        Log.d(TAG, "onRiddleSelected");

        Riddle selectedRiddle = null;
        for (Riddle riddle : mRiddles) {
            if (riddle.getId() == riddleId) {
                selectedRiddle = riddle;
            }
        }
//        Log.d(TAG, "Selected riddle active: " + selectedRiddle.isActive() + " solved: " + selectedRiddle.isRiddleSolved());
        if (selectedRiddle != null && (selectedRiddle.isActive() || selectedRiddle.isRiddleSolved())) {
            if (mTwoPane) {
                RiddleFragment riddleFragment = RiddleFragment.newInstance(selectedRiddle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.riddle_container, riddleFragment)
                        .commit();
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable(RiddleFragment.BUNDLE_RIDDLE, selectedRiddle);

                final Intent intent = new Intent(this, RiddleActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, getString(R.string.cannot_be_selected), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");

        String selection = RiddleContract.Entry.COLUMN_GAME_UUID + " = ?";
        String[] selectionArgs = {mGame.getUuid().toString()};
        return new CursorLoader(this,
                RiddleContract.Entry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                RiddleContract.Entry.COLUMN_NO);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Riddles loaded: " + String.valueOf(data.getCount()));
        Log.d(TAG, String.valueOf(loader.getId()));

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        switch (loader.getId()) {
            case RIDDLES_FIRST_ATTEMPT_LOADER_ID:
                if (data.getCount() != 0) {
                    mRiddles = RiddleCursorUtils.getRiddlesFromCursor(data);
                    Log.d(TAG, "Riddles loaded and in mRiddles: " + mRiddles.size());
                    setupFragments();
                } else {
                    loadRiddlesFromInternet();
                }
                break;
            case RIDDLES_SECOND_ATTEMPT_LOADER_ID:
                if (data.getCount() != 0) {
                    mRiddles = RiddleCursorUtils.getRiddlesFromCursor(data);
                    Log.d(TAG, "Riddles loaded and in mRiddles: " + mRiddles.size());
                    setupFragments();
                } else {
                    showErrorMessage();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onCorrectLocation(Riddle riddle) {
        for (int i = 0; i < mRiddles.size(); i++) {
            if (mRiddles.get(i).getId() == riddle.getId()) {
                mRiddles.set(i, riddle);
            }
        }
        RiddleListFragment riddleListFragment = RiddleListFragment.newInstance(mRiddles);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.game_container, riddleListFragment)
                .commit();
    }

    @Override
    public void onCorrectAnswer(Riddle riddle, Riddle nextRiddle) {
        for (int i = 0; i < mRiddles.size(); i++) {
            if (mRiddles.get(i).getId() == riddle.getId()) {
                mRiddles.set(i, riddle);
            }
            if (nextRiddle != null && mRiddles.get(i).getId() == nextRiddle.getId()) {
                mRiddles.set(i, nextRiddle);
            }
        }
        RiddleListFragment riddleListFragment = RiddleListFragment.newInstance(mRiddles);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.game_container, riddleListFragment)
                .commit();
        if (nextRiddle != null && mTwoPane) {
            RiddleFragment riddleFragment = RiddleFragment.newInstance(nextRiddle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.riddle_container, riddleFragment)
                    .commit();
        }
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "DownloadReceiver onReceiveResult");

            super.onReceiveResult(resultCode, resultData);

            switch (resultCode) {
                case DownloadRiddlesIntentService.DOWNLOAD_SUCCESS:
                    mLoadingIndicator.setProgress(50);
                    break;
                case DownloadRiddlesIntentService.SAVE_SUCCESS:
                    mLoadingIndicator.setProgress(100);
                    mLoadingIndicator.setVisibility(View.GONE);
                    showDataView();
                    loadRiddlesFromDb(RIDDLES_SECOND_ATTEMPT_LOADER_ID);
                    break;
                case DownloadRiddlesIntentService.DOWNLOAD_ERROR:
                case DownloadRiddlesIntentService.SAVE_ERROR:
                    mLoadingIndicator.setVisibility(View.GONE);
                    showErrorMessage();
                    break;

            }
        }
    }
}
