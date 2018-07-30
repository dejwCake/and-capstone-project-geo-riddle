package sk.dejw.android.georiddles.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.provider.RiddleContract;
import sk.dejw.android.georiddles.provider.RiddleProvider;
import sk.dejw.android.georiddles.utils.RiddleCursorUtils;

public class GameActivity extends AppCompatActivity implements RiddleListFragment.OnRiddleClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String BUNDLE_GAME = "game";
    public static final String BUNDLE_RIDDLES = "riddles";

    private static final int RIDDLES_FIRST_ATTEMPT_LOADER_ID = 152;
    private static final int RIDDLES_SECOND_ATTEMPT_LOADER_ID = 153;

    Game mGame = null;
    ArrayList<Riddle> mRiddles;

    private boolean mTwoPane;

    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

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

        //TODO download riddles if not downloaded yet.
        if(mRiddles.size() == 0) {
            loadRiddlesFromDb();
        } else {
            setupFragments(savedInstanceState);
        }

    }

    private void setupFragments(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            RiddleListFragment riddleListFragment = RiddleListFragment.newInstance(mRiddles);
            fragmentManager.beginTransaction()
                    .add(R.id.game_container, riddleListFragment)
                    .commit();
        }

        if (findViewById(R.id.riddle_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                RiddleFragment riddleFragment = new RiddleFragment();
//                riddleFragment.setRecipe(mRecipe);
//                riddleFragment.setRecipeStepPosition(0);

                fragmentManager.beginTransaction()
                        .add(R.id.riddle_container, riddleFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    private void loadRiddlesFromDb() {
        Log.d(TAG, "loadRiddlesFromDb");

        mLoadingIndicator.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(RIDDLES_FIRST_ATTEMPT_LOADER_ID, null, this);
    }

    @Override
    public void onRiddleSelected(int riddleId) {
        Log.d(TAG, "onRiddleSelected");
        if (mTwoPane) {
            RiddleFragment riddleFragment = new RiddleFragment();
//            riddleFragment.setRiddle(mRecipe);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.riddle_container, riddleFragment)
                    .commit();
        } else {
            //TODO add activity
//            Bundle bundle = new Bundle();
//
//            final Intent intent = new Intent(this, RiddleActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_GAME, mGame);
        outState.putParcelableArrayList(BUNDLE_RIDDLES, mRiddles);
        super.onSaveInstanceState(outState);
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

        if(loader.getId() == RIDDLES_FIRST_ATTEMPT_LOADER_ID) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data.getCount() != 0) {
                mRiddles = RiddleCursorUtils.getRiddlesFromCursor(data);
                setupFragments();
            } else {
                showErrorMessage();
            }
        }
        if (data.getCount() != 0) {
            showDataView();
            swapData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
