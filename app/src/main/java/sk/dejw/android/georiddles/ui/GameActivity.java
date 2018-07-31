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
import sk.dejw.android.georiddles.provider.RiddleContract;
import sk.dejw.android.georiddles.provider.RiddleProvider;
import sk.dejw.android.georiddles.services.DownloadRiddlesIntentService;
import sk.dejw.android.georiddles.utils.cursor.RiddleCursorUtils;
import sk.dejw.android.georiddles.utils.network.GlobalNetworkUtils;

public class GameActivity extends AppCompatActivity implements RiddleListFragment.OnRiddleClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String BUNDLE_GAME = "game";
    public static final String BUNDLE_RIDDLES = "riddles";
    public static final int REQUEST_CODE = 1256;

    private static final int RIDDLES_FIRST_ATTEMPT_LOADER_ID = 152;
    private static final int RIDDLES_SECOND_ATTEMPT_LOADER_ID = 153;

    private Game mGame = null;
    private ArrayList<Riddle> mRiddles;
    private Bundle mSavedInstanceState;

    private boolean mTwoPane;

    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.game_main_content)
    LinearLayout mGameMainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            Log.d(TAG, "savedInstanceState: " + savedInstanceState.toString());
            if (mGame == null && savedInstanceState.containsKey(BUNDLE_GAME)) {
                mGame = savedInstanceState.getParcelable(BUNDLE_GAME);
            }
            if (savedInstanceState.containsKey(BUNDLE_RIDDLES)) {
                mRiddles = savedInstanceState.getParcelableArrayList(BUNDLE_RIDDLES);
            }
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
        super.onResume();

        if (mRiddles == null || mRiddles.size() == 0) {
            loadRiddlesFromDb(RIDDLES_FIRST_ATTEMPT_LOADER_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_GAME, mGame);
        outState.putParcelableArrayList(BUNDLE_RIDDLES, mRiddles);
        super.onSaveInstanceState(outState);
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

    private void setupFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mSavedInstanceState == null) {
            RiddleListFragment riddleListFragment = RiddleListFragment.newInstance(mRiddles);
            fragmentManager.beginTransaction()
                    .add(R.id.game_container, riddleListFragment)
                    .commit();
        }

        if (findViewById(R.id.riddle_container) != null) {
            mTwoPane = true;

            if (mSavedInstanceState == null) {

                RiddleFragment riddleFragment = new RiddleFragment();
                Riddle activeRiddle = null;
                for (Riddle riddle : mRiddles) {
                    if (riddle.isActive() && activeRiddle == null) {
                        activeRiddle = riddle;
                    }
                }
                if (activeRiddle == null) {
                    activeRiddle = mRiddles.get(0);
                }
                riddleFragment.setRiddle(activeRiddle);

                fragmentManager.beginTransaction()
                        .add(R.id.riddle_container, riddleFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
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
    public void onRiddleSelected(int riddleId) {
        Log.d(TAG, "onRiddleSelected");
        Riddle selectedRiddle = null;
        for (Riddle riddle : mRiddles) {
            if (riddle.getId() == riddleId) {
                selectedRiddle = riddle;
            }
        }
        if (selectedRiddle != null && (selectedRiddle.isActive() || selectedRiddle.isRiddleSolved())) {
            if (mTwoPane) {
                RiddleFragment riddleFragment = new RiddleFragment();
                riddleFragment.setRiddle(selectedRiddle);

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
        String selection = RiddleContract.COLUMN_GAME_UUID + " = ?";
        String[] selectionArgs = {mGame.getUuid().toString()};
        return new CursorLoader(this,
                RiddleProvider.Riddles.RIDDLES_URI,
                null,
                selection,
                selectionArgs,
                RiddleContract.COLUMN_NO);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Games loaded: " + String.valueOf(data.getCount()));

        switch (loader.getId()) {
            case RIDDLES_FIRST_ATTEMPT_LOADER_ID:
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (data.getCount() != 0) {
                    mRiddles = RiddleCursorUtils.getRiddlesFromCursor(data);
                    setupFragments();
                } else {
                    loadRiddlesFromInternet();
                }
                break;
            case RIDDLES_SECOND_ATTEMPT_LOADER_ID:
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (data.getCount() != 0) {
                    mRiddles = RiddleCursorUtils.getRiddlesFromCursor(data);
                    setupFragments();
                } else {
                    showErrorMessage();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            switch (resultCode) {
                case DownloadRiddlesIntentService.DOWNLOAD_SUCCESS:
                    mLoadingIndicator.setProgress(50);
                    break;
                case DownloadRiddlesIntentService.SAVE_SUCCESS:
                    mLoadingIndicator.setProgress(100);
                    showDataView();
                    loadRiddlesFromDb(RIDDLES_SECOND_ATTEMPT_LOADER_ID);
                    break;
                case DownloadRiddlesIntentService.DOWNLOAD_ERROR:
                case DownloadRiddlesIntentService.SAVE_ERROR:
                    showErrorMessage();
                    break;

            }
        }
    }
}
