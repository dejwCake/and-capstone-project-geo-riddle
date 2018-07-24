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

public class GamesSpinnerArrayAdapter extends ArrayAdapter<Game> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Game> items;
    private final int mResource;

    @BindView(R.id.tv_game_title)
    TextView mGameTitle;

    public GamesSpinnerArrayAdapter(@NonNull Context context, int resource, @NonNull List<Game> objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
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

        Game game = items.get(position);

        mGameTitle.setText(game.getTitle());

        return view;
    }
}
