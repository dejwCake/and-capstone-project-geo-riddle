package sk.dejw.android.georiddles.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.GamesSpinnerArrayAdapter;
import sk.dejw.android.georiddles.asyncTask.AsyncTaskCompleteListener;
import sk.dejw.android.georiddles.asyncTask.FetchGamesTask;
import sk.dejw.android.georiddles.asyncTask.SaveGamesTask;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.provider.GameContract;
import sk.dejw.android.georiddles.provider.GameProvider;
import sk.dejw.android.georiddles.utils.GameCursorUtils;
import sk.dejw.android.georiddles.utils.network.GlobalNetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_GAMES = "games";

    private static final int GAMES_LOADER_ID = 144;

    private ArrayList<Game> mListOfGames;
    private GamesSpinnerArrayAdapter mAdapter;

    @BindView(R.id.cl_main)
    ConstraintLayout mMainLayout;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.et_code)
    EditText mCode;
    @BindView(R.id.s_nearby)
    Spinner mNearby;
    @BindView(R.id.iv_search)
    ImageView mSearch;
    @BindView(R.id.bt_start)
    Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_GAMES)) {
            mListOfGames = new ArrayList<Game>();
        } else {
            mListOfGames = savedInstanceState.getParcelableArrayList(BUNDLE_GAMES);
        }
        ButterKnife.bind(this);

        mAdapter = new GamesSpinnerArrayAdapter(this,
                R.layout.game_spinner_item, mListOfGames);
        mNearby.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadDataFromInternet();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_GAMES, mListOfGames);
        super.onSaveInstanceState(outState);
    }

    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage");

        mMainLayout.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        Log.d(TAG, "showDataView");

        mErrorMessage.setVisibility(View.INVISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);
    }

    private void loadDataFromInternet() {
        Log.d(TAG, "loadDataFromInternet");

        if (GlobalNetworkUtils.hasConnection(this)) {
            Log.d(TAG, "Internet working.");

            mLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchGamesTask(this, new FetchGamesTaskCompleteListener()).execute();
        } else {
            Log.d(TAG, "Internet not working.");

            loadDataFromDb();
        }
    }

    private void loadDataFromDb() {
        Log.d(TAG, "loadDataFromDb");

        mLoadingIndicator.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(GAMES_LOADER_ID, null, this);
    }

    private void saveDataFromInternet(Game[] games) {
        Log.d(TAG, "saveDataFromInternet");

        mLoadingIndicator.setVisibility(View.VISIBLE);
        new SaveGamesTask(this, new SaveGamesTaskCompleteListener()).execute(games);
    }

    private void swapData(Cursor cursor) {
        Log.d(TAG, "Swapping for games: " + cursor.getCount());

        ArrayList<Game> listOfGames = GameCursorUtils.getGamesFromCursor(cursor);
        Log.d(TAG, "Swapping for games: " + listOfGames.size());
        mAdapter.swapData(listOfGames);
        mAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,
                GameProvider.Games.GAMES_URI,
                null,
                null,
                null,
                GameContract.COLUMN_UUID);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Games loaded: " + String.valueOf(data.getCount()));

        mLoadingIndicator.setVisibility(View.INVISIBLE);
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

    public class FetchGamesTaskCompleteListener implements AsyncTaskCompleteListener<Game[]>
    {
        @Override
        public void onTaskComplete(Game[] games)
        {
            Log.d(TAG, "Games downloaded: ".concat(String.valueOf(games.length)));

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (games != null) {
                saveDataFromInternet(games);
            } else {
                showErrorMessage();
            }
        }
    }

    public class SaveGamesTaskCompleteListener implements AsyncTaskCompleteListener<String>
    {
        @Override
        public void onTaskComplete(String result)
        {
            Log.d(TAG, "Recipes saved to db: " + result);

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result.equals(SaveGamesTask.OK_RESULT)) {
                loadDataFromDb();
            } else {
                showErrorMessage();
            }
        }
    }
}
