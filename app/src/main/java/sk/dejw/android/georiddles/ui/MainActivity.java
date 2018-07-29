package sk.dejw.android.georiddles.ui;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    @BindView(R.id.bt_near_by)
    Button mNearBy;
    @BindView(R.id.iv_search)
    ImageView mSearch;

    ListPopupWindow mListPopupWindow;

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

        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(mAdapter);
        mListPopupWindow.setAnchorView(mNearBy);
        mListPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setHeight(400);

        mListPopupWindow.setModal(true);
        mListPopupWindow.setOnItemClickListener(
                new OnNearByItemClickListener());
        mNearBy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListPopupWindow.show();
            }
        });

        mSearch.setOnClickListener(new OnSearchButtonClickListener());
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

    private void showConfirmGameDialog(final Game game) {
        Log.d(TAG, "showConfirmGameDialog");

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm_dialog_title))
                .setMessage(getString(R.string.confirm_dialog_text).concat(game.getTitle()))
                .setPositiveButton(R.string.confirm_dialog_start_game, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(game);
                    }
                })
                .setNegativeButton(R.string.confirm_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void startGame(Game game) {
        Log.d(TAG, "startGame");

        Toast.makeText(this, "New activity will be loaded", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, RecipeDetailActivity.class);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe);
//        startActivity(intent);
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
        Log.d(TAG, "Swapping for games in cursor: " + cursor.getCount());

        ArrayList<Game> listOfGames = GameCursorUtils.getGamesFromCursor(cursor);
        Log.d(TAG, "Swapping for games in list: " + listOfGames.size());
//        mAdapter.swapData(listOfGames);
        mAdapter = new GamesSpinnerArrayAdapter(this,
                R.layout.game_spinner_item, listOfGames);
        mListPopupWindow.setAdapter(mAdapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //TODO Load only near by
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

    public class FetchGamesTaskCompleteListener implements AsyncTaskCompleteListener<Game[]> {
        @Override
        public void onTaskComplete(Game[] games) {
            Log.d(TAG, "Games downloaded: ".concat(String.valueOf(games.length)));

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (games != null) {
                saveDataFromInternet(games);
            } else {
                showErrorMessage();
            }
        }
    }

    public class SaveGamesTaskCompleteListener implements AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskComplete(String result) {
            Log.d(TAG, "Recipes saved to db: " + result);

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result.equals(SaveGamesTask.OK_RESULT)) {
                loadDataFromDb();
            } else {
                showErrorMessage();
            }
        }
    }

    class OnSearchButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String code = mCode.getText().toString();
            if (!code.equals("")) {
                String selection = GameContract.COLUMN_CODE + " = ?";
                String[] selectionArgs = {code};
                Cursor searchResult = getContentResolver().query(
                        GameProvider.Games.GAMES_URI,
                        null,
                        selection,
                        selectionArgs,
                        GameContract.COLUMN_UUID);
                Game game = GameCursorUtils.getFirstGameFromCursor(searchResult);
                if (game != null) {
                    showConfirmGameDialog(game);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.code_not_found), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class OnNearByItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListPopupWindow.dismiss();
            Game game = (Game) parent.getItemAtPosition(position);
            showConfirmGameDialog(game);
        }
    }
}
