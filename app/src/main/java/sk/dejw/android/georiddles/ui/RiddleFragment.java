package sk.dejw.android.georiddles.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 222;

    private Riddle mRiddle;
    private RiddlePagerAdapter mRiddlePagerAdapter;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

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
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRiddle = getArguments().getParcelable(BUNDLE_RIDDLE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

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
        if (!mRiddle.isLocationChecked()) {
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
        Log.d(TAG, "setRiddle");

        mRiddle = riddle;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelable(BUNDLE_RIDDLE, mRiddle);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");

        mGoogleMap = googleMap;

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMinZoomPreference(11);

        LatLng riddleLocation = new LatLng(mRiddle.getGpsLat(), mRiddle.getGpsLng());
        googleMap.addMarker(new MarkerOptions().position(riddleLocation)
                .title(mRiddle.getTitle()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riddleLocation, 10));

        setMyLocation();
    }

    private void setMyLocation() {
        Log.d(TAG, "setMyLocation");

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
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
            if (!locationNotAllowed) {
                setMyLocation();
            }
        }
    }

    @Override
    public void onLocation() {
        Log.d(TAG, "onLocation");

        //TODO save to db, that location has been checked and also update current riddle

//        mRiddlePagerAdapter.swapFragmentAtPosition(0, RiddleQuestionFragment.newInstance(mRiddle), getString(R.string.tab_question));
        mRiddlePagerAdapter = new RiddlePagerAdapter(getChildFragmentManager());
        mRiddlePagerAdapter.addFragment(RiddleQuestionFragment.newInstance(mRiddle), getString(R.string.tab_question));
        mRiddlePagerAdapter.addFragment(mMapFragment, getString(R.string.tab_map));
        mViewPager.setAdapter(mRiddlePagerAdapter);
    }
}
