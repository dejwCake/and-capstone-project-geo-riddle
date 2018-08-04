package sk.dejw.android.georiddles.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.GamesArrayAdapter;
import sk.dejw.android.georiddles.asyncTasks.AsyncTaskCompleteListener;
import sk.dejw.android.georiddles.asyncTasks.FetchGamesTask;
import sk.dejw.android.georiddles.asyncTasks.SaveGamesTask;
import sk.dejw.android.georiddles.models.Game;
import sk.dejw.android.georiddles.providers.GameContract;
import sk.dejw.android.georiddles.providers.GameProvider;
import sk.dejw.android.georiddles.utils.GeoRiddlesState;
import sk.dejw.android.georiddles.utils.cursor.GameCursorUtils;
import sk.dejw.android.georiddles.utils.network.GlobalNetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_GAMES = "games";

    private static final int GAMES_LOADER_ID = 144;
    private static final int REQUEST_CHECK_SETTINGS = 153;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int SQUARE_RADIUS_IN_DEGREES = 2;

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

    private ArrayList<Game> mListOfGames;
    private GamesArrayAdapter mAdapter;

    private ListPopupWindow mListPopupWindow;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    private boolean mDataError = false;
    private boolean mLocationError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_GAMES)) {
            mListOfGames = new ArrayList<Game>();
        } else {
            mListOfGames = savedInstanceState.getParcelableArrayList(BUNDLE_GAMES);
        }

        mAdapter = new GamesArrayAdapter(this,
                R.layout.game_spinner_item, mListOfGames);

        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(mAdapter);
        mListPopupWindow.setAnchorView(mNearBy);
        mListPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setHeight(300);

        mListPopupWindow.setModal(true);
        mListPopupWindow.setOnItemClickListener(
                new OnNearByItemClickListener());
        mNearBy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListPopupWindow.show();
            }
        });

        mSearch.setOnClickListener(new OnSearchButtonClickListener());

        GeoRiddlesState.clearLastSelectedGame(this);

        initializeLocation();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        loadDataFromInternet();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelableArrayList(BUNDLE_GAMES, mListOfGames);
        super.onSaveInstanceState(outState);
    }

    private void updateUi() {
        Log.d(TAG, "updateUi");

        if (mDataError) {
            showErrorMessage(getString(R.string.error_message_data));
        } else if (mLocationError) {
            showErrorMessage(getString(R.string.error_message_location));
        } else {
            showDataView();
        }
    }

    private void showErrorMessage(String errorMessage) {
        Log.d(TAG, "showErrorMessage");

        mMainLayout.setVisibility(View.INVISIBLE);
        if (errorMessage.isEmpty()) {
            mErrorMessage.setText(getString(R.string.error_message));
        }
        mErrorMessage.setText(errorMessage);
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
                .setMessage(getString(R.string.confirm_dialog_text).concat(" ").concat(game.getTitle()))
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

        GeoRiddlesState.saveLastSelectedGame(this, game.getId());

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.BUNDLE_GAME, game);
        startActivity(intent);
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
        mAdapter = new GamesArrayAdapter(this,
                R.layout.game_spinner_item, listOfGames);
        mListPopupWindow.setAdapter(mAdapter);
    }

    private void initializeLocation() {
        Log.d(TAG, "initializeLocation");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        checkLocationSettings();
    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void checkLocationSettings() {
        Log.d(TAG, "checkLocationSettings");

        /**
         * Based on https://developer.android.com/training/location/change-location-settings
         */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {

                    }
                }
            }
        });
    }

    private void getLocation() {
        Log.d(TAG, "getLocation");

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                            loadDataFromDb();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationError = true;
                        updateUi();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            boolean locationNotAllowed = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] < 0) {
                        locationNotAllowed = true;
                    }
                }
            }
            if (locationNotAllowed) {
                mLocationError = true;
                updateUi();
            } else {
                getLocation();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");

        if (mLocation != null) {
            double top = mLocation.getLatitude() + (SQUARE_RADIUS_IN_DEGREES / 2);
            double bottom = mLocation.getLatitude() - (SQUARE_RADIUS_IN_DEGREES / 2);
            double left = mLocation.getLongitude() - (SQUARE_RADIUS_IN_DEGREES / 2);
            double right = mLocation.getLongitude() + (SQUARE_RADIUS_IN_DEGREES / 2);
            String selection =
                    GameContract.COLUMN_GPS_LAT + " <= ? AND " +
                            GameContract.COLUMN_GPS_LAT + " >= ? AND " +
                            GameContract.COLUMN_GPS_LNG + " <= ? AND " +
                            GameContract.COLUMN_GPS_LNG + " >= ?";
            String[] selectionArgs = {
                    String.valueOf(top),
                    String.valueOf(bottom),
                    String.valueOf(right),
                    String.valueOf(left)
            };
            return new CursorLoader(this,
                    GameProvider.Games.GAMES_URI,
                    null,
                    selection,
                    selectionArgs,
                    GameContract.COLUMN_UUID);
        } else {
            Log.d(TAG, "we are loading all games");
            return new CursorLoader(this,
                    GameProvider.Games.GAMES_URI,
                    null,
                    null,
                    null,
                    GameContract.COLUMN_UUID);

        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Games loaded: " + String.valueOf(data.getCount()));

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data.getCount() != 0) {
            updateUi();
            swapData(data);
        } else {
            swapData(data);
            Toast.makeText(this, getString(R.string.no_nearby_games), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    public class FetchGamesTaskCompleteListener implements AsyncTaskCompleteListener<Game[]> {
        @Override
        public void onTaskComplete(Game[] games) {
            Log.d(TAG, "Games downloaded: ".concat(String.valueOf(games.length)));

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (games != null) {
                saveDataFromInternet(games);
            } else {
                mDataError = true;
                updateUi();
            }
        }
    }

    public class SaveGamesTaskCompleteListener implements AsyncTaskCompleteListener<String> {
        @Override
        public void onTaskComplete(String result) {
            Log.d(TAG, "Games saved to db: " + result);

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result.equals(SaveGamesTask.OK_RESULT)) {
                loadDataFromDb();
            } else {
                mDataError = true;
                updateUi();
            }
        }
    }

    class OnSearchButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnSearchButtonClickListener onClick");

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
            Log.d(TAG, "OnNearByItemClickListener onItemClick");

            mListPopupWindow.dismiss();
            Game game = (Game) parent.getItemAtPosition(position);
            showConfirmGameDialog(game);
        }
    }
}
