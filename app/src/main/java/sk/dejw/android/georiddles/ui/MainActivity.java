package sk.dejw.android.georiddles.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.GamesSpinnerArrayAdapter;
import sk.dejw.android.georiddles.models.Game;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BUNDLE_GAMES = "games";

    private ArrayList<Game> mListOfGames;

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
    }
}
