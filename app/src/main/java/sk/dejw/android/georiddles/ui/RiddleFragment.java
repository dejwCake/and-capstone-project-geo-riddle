package sk.dejw.android.georiddles.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleFragment extends Fragment {
    private static final String TAG = RiddleFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";

    private Riddle mRiddle;

    public RiddleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRiddle = savedInstanceState.getParcelable(BUNDLE_RIDDLE);
        }

        View rootView = inflater.inflate(R.layout.fragment_riddle, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    public void setRiddle(Riddle riddle) {
        mRiddle = riddle;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(BUNDLE_RIDDLE, mRiddle);
        super.onSaveInstanceState(outState);
    }
}
