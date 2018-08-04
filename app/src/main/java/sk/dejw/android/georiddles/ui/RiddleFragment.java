package sk.dejw.android.georiddles.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.RiddlePagerAdapter;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.RiddleContract;
import sk.dejw.android.georiddles.providers.RiddleProvider;

public class RiddleFragment extends Fragment implements OnMapReadyCallback,
        RiddleDirectionsAndQuestionFragment.OnCorrectLocationListener,
        RiddleDirectionsAndQuestionFragment.OnCorrectAnswerListener {
    private static final String TAG = RiddleFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 222;
    private static final int LOCATION_REQUEST_INTERVAL = 5 * 1000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 3 * 1000;

    @BindView(R.id.vp_riddle)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    private Riddle mRiddle;
    private RiddlePagerAdapter mRiddlePagerAdapter;
    private RiddleDirectionsAndQuestionFragment mRiddleDirectionsAndQuestionFragment;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private CustomLocationCallback mCustomLocationCallback;

    public OnCorrectLocationListener mCorrectLocationCallback;
    public OnCorrectAnswerListener mCorrectAnswerCallback;

    public interface OnCorrectLocationListener {
        void onCorrectLocation(Riddle riddle);
    }

    public interface OnCorrectAnswerListener {
        void onCorrectAnswer(Riddle riddle, Riddle nextRiddle);
    }

    public RiddleFragment() {
    }

    public static RiddleFragment newInstance(Riddle riddle) {
        Log.d(TAG, "newInstance");

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
//        setRetainInstance(true);

        initializeLocation();
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

        mRiddlePagerAdapter = new RiddlePagerAdapter(getChildFragmentManager());

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof RiddleDirectionsAndQuestionFragment) {
                mRiddleDirectionsAndQuestionFragment = (RiddleDirectionsAndQuestionFragment) fragment;
            } else if (fragment instanceof SupportMapFragment) {
                mMapFragment = (SupportMapFragment) fragment;
            }
        }

        if (mRiddleDirectionsAndQuestionFragment == null) {
            mRiddleDirectionsAndQuestionFragment = RiddleDirectionsAndQuestionFragment.newInstance(mRiddle);
        }
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
        }
        mMapFragment.getMapAsync(this);

        /**
         * Based on https://stackoverflow.com/questions/41413150/fragment-tabs-inside-fragment
         */
        mRiddlePagerAdapter.addFragment(mRiddleDirectionsAndQuestionFragment, getString(R.string.tab_directions));
        mRiddlePagerAdapter.addFragment(mMapFragment, getString(R.string.tab_map));
        mViewPager.setAdapter(mRiddlePagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        Log.d(TAG, "Riddle: " + mRiddle.getTitle());

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        if (!mRiddle.isLocationChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);

        try {
            mCorrectLocationCallback = (OnCorrectLocationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCorrectLocationListener");
        }

        try {
            mCorrectAnswerCallback = (OnCorrectAnswerListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCorrectAnswerListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mCorrectLocationCallback = null;
        mCorrectAnswerCallback = null;
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

        LatLng riddleLocation = new LatLng(mRiddle.getGpsLat(), mRiddle.getGpsLng());
        googleMap.addMarker(new MarkerOptions().position(riddleLocation).title(mRiddle.getTitle()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riddleLocation, 10));

        enableUserLocationOnMap();
    }

    private void moveMap(Location userLocation) {
        if (mGoogleMap != null) {
            double southWestLat = mRiddle.getGpsLat() < userLocation.getLatitude() ? mRiddle.getGpsLat() : userLocation.getLatitude();
            double southWestLng = mRiddle.getGpsLng() < userLocation.getLatitude() ? mRiddle.getGpsLat() : userLocation.getLatitude();
            double northEastLat = mRiddle.getGpsLat() > userLocation.getLatitude() ? mRiddle.getGpsLat() : userLocation.getLatitude();
            double northEastLng = mRiddle.getGpsLng() > userLocation.getLatitude() ? mRiddle.getGpsLat() : userLocation.getLatitude();
            LatLngBounds area = new LatLngBounds(new LatLng(southWestLat, southWestLng), new LatLng(northEastLat, northEastLng));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(area, 10));
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
                enableUserLocationOnMap();
                if (mRiddle.isLocationChecked()) {
                    startLocationUpdates();
                }
            }
        }
    }

    private void initializeLocation() {
        Log.d(TAG, "initializeLocation");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mCustomLocationCallback = new CustomLocationCallback();

        createLocationRequest();

        checkLocationSettings();
    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void checkLocationSettings() {
        Log.d(TAG, "checkLocationSettings");

        /**
         * Based on https://developer.android.com/training/location/change-location-settings
         */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        client.checkLocationSettings(builder.build());
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mCustomLocationCallback, null);
    }

    private void enableUserLocationOnMap() {
        Log.d(TAG, "enableUserLocationOnMap");

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");

        mFusedLocationClient.removeLocationUpdates(mCustomLocationCallback);
    }

    public void setRiddle(Riddle riddle) {
        Log.d(TAG, "setRiddle");

        mRiddle = riddle;
    }

    @Override
    public void onCorrectLocation() {
        Log.d(TAG, "onCorrectLocation");

        stopLocationUpdates();
        mRiddle.setLocationChecked(true);

        ContentValues updateRiddle = new ContentValues();
        updateRiddle.put(RiddleContract.COLUMN_LOCATION_CHECKED, true);
        getActivity().getContentResolver().update(RiddleProvider.Riddles.withId(mRiddle.getId()), updateRiddle, null, null);

        mRiddleDirectionsAndQuestionFragment.setRiddle(mRiddle);
        mRiddleDirectionsAndQuestionFragment.updateUi();

        mCorrectLocationCallback.onCorrectLocation(mRiddle);
    }

    @Override
    public void onCorrectAnswer(Riddle nextRiddle) {
        Log.d(TAG, "onCorrectAnswer");

        mRiddle.setRiddleSolved(true);
        mRiddle.setActive(false);

        ContentValues updateRiddle = new ContentValues();
        updateRiddle.put(RiddleContract.COLUMN_RIDDLE_SOLVED, true);
        updateRiddle.put(RiddleContract.COLUMN_ACTIVE, false);
        getActivity().getContentResolver().update(RiddleProvider.Riddles.withId(mRiddle.getId()), updateRiddle, null, null);

        if (nextRiddle != null) {
            nextRiddle.setActive(true);
            ContentValues updateNextRiddle = new ContentValues();
            updateNextRiddle.put(RiddleContract.COLUMN_ACTIVE, true);
            getActivity().getContentResolver().update(RiddleProvider.Riddles.withId(nextRiddle.getId()), updateNextRiddle, null, null);
        }

        //Just change the riddle in map and in RiddleDirectionsAndQuestionFragment
        //However do not know if it is a good solution

        //or send it back to activity and handle it there
        //For now I have used the second option

        mCorrectAnswerCallback.onCorrectAnswer(mRiddle, nextRiddle);
    }

    class CustomLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "CustomLocationCallback onLocationResult");

            if (locationResult == null) {
                return;
            }
            Location userLocation = locationResult.getLastLocation();
            Log.d(TAG, "New location is: " + userLocation.toString());
            mRiddleDirectionsAndQuestionFragment.setUserLocation(userLocation);
            mRiddleDirectionsAndQuestionFragment.updateUi();
//            moveMap(userLocation);
        }
    }
}
