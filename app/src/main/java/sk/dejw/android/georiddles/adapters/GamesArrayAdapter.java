package sk.dejw.android.georiddles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Game;

public class GamesArrayAdapter extends ArrayAdapter<Game> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mResource;
    private List<Game> mGamesList;

    @BindView(R.id.tv_game_title)
    TextView mGameTitle;

    public GamesArrayAdapter(@NonNull Context context, int resource, @NonNull List<Game> games) {
        super(context, resource, games);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mGamesList = games;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        ButterKnife.bind(this, view);

        Game game = mGamesList.get(position);

        mGameTitle.setText(game.getTitle());

        return view;
    }
}
