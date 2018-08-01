package sk.dejw.android.georiddles.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.RiddlePagerAdapter;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleFragment extends Fragment {
    private static final String TAG = RiddleFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";

    private Riddle mRiddle;

    @BindView(R.id.vp_riddle)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    public RiddleFragment() {
    }

    public static RiddleFragment newInstance(Riddle riddle) {
        RiddleFragment fragment = new RiddleFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_RIDDLE, riddle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRiddle = getArguments().getParcelable(BUNDLE_RIDDLE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRiddle = savedInstanceState.getParcelable(BUNDLE_RIDDLE);
        }

        View rootView = inflater.inflate(R.layout.fragment_riddle, container, false);
        ButterKnife.bind(this, rootView);

        RiddlePagerAdapter riddlePagerAdapter = new RiddlePagerAdapter(getChildFragmentManager());
        riddlePagerAdapter.addFragment(RiddleDirectionsFragment.newInstance(mRiddle), getString(R.string.directions));
        riddlePagerAdapter.addFragment(SupportMapFragment.newInstance(), getString(R.string.map));
        mViewPager.setAdapter(riddlePagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

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
