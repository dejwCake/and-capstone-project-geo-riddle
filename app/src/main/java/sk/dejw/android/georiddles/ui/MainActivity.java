package sk.dejw.android.georiddles.ui;

import android.support.constraint.ConstraintLayout;
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
import sk.dejw.android.georiddles.utils.network.GlobalNetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_GAMES = "games";

    private ArrayList<Game> mListOfGames;

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

        GamesSpinnerArrayAdapter adapter = new GamesSpinnerArrayAdapter(this,
                R.layout.game_spinner_item, mListOfGames);
        mNearby.setAdapter(adapter);
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

    private void loadDataFromInternet() {
        Log.d(TAG, "loadDataFromInternet");

        if (GlobalNetworkUtils.hasConnection(this)) {
            Log.d(TAG, "Internet working.");

            mLoadingIndicator.setVisibility(View.VISIBLE);
            new FetchGamesTask(this, new FetchGamesTaskCompleteListener()).execute();
        } else {
            Log.d(TAG, "Internet not working.");

//            loadDataFromDb();
        }
    }

    private void saveDataFromInternet(Game[] games) {
        Log.d(TAG, "saveDataFromInternet");

        mLoadingIndicator.setVisibility(View.VISIBLE);
        new SaveGamesTask(this, new SaveGamesTaskCompleteListener()).execute(games);
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
//                loadDataFromDb();
            } else {
                showErrorMessage();
            }
        }
    }
}
