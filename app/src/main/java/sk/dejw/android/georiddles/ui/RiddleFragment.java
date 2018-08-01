package sk.dejw.android.georiddles.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.RiddlePagerAdapter;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleFragment extends Fragment implements OnMapReadyCallback, RiddleDirectionsFragment.OnCheckLocationClickListener {
    private static final String TAG = RiddleFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";

    private Riddle mRiddle;
    private RiddlePagerAdapter mRiddlePagerAdapter;
    private SupportMapFragment mMapFragment;

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

        mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        /**
         * Based on https://stackoverflow.com/questions/41413150/fragment-tabs-inside-fragment
         */
        mRiddlePagerAdapter = new RiddlePagerAdapter(getChildFragmentManager());
        if(!mRiddle.isLocationChecked()) {
            mRiddlePagerAdapter.addFragment(RiddleDirectionsFragment.newInstance(mRiddle), getString(R.string.tab_directions));
        } else {
            mRiddlePagerAdapter.addFragment(RiddleQuestionFragment.newInstance(mRiddle), getString(R.string.tab_question));
        }
        mRiddlePagerAdapter.addFragment(mMapFragment, getString(R.string.tab_map));
        mViewPager.setAdapter(mRiddlePagerAdapter);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng riddleLocation = new LatLng(mRiddle.getGpsLat(), mRiddle.getGpsLng());
        googleMap.addMarker(new MarkerOptions().position(riddleLocation)
                .title(mRiddle.getTitle()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riddleLocation, 10));
    }

    @Override
    public void onLocation() {
        //TODO save to db, that location has been checked and also update current riddle

//        mRiddlePagerAdapter.swapFragmentAtPosition(0, RiddleQuestionFragment.newInstance(mRiddle), getString(R.string.tab_question));
        mRiddlePagerAdapter = new RiddlePagerAdapter(getChildFragmentManager());
        mRiddlePagerAdapter.addFragment(RiddleQuestionFragment.newInstance(mRiddle), getString(R.string.tab_question));
        mRiddlePagerAdapter.addFragment(mMapFragment, getString(R.string.tab_map));
        mViewPager.setAdapter(mRiddlePagerAdapter);
    }
}
