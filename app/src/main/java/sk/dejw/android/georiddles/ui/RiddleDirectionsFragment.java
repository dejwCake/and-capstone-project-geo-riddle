package sk.dejw.android.georiddles.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleDirectionsFragment extends Fragment {
    private static final String TAG = RiddleDirectionsFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";

    private Riddle mRiddle;
    public OnCheckLocationClickListener mCallback;

    @BindView(R.id.iv_image)
    ImageView mImage;
    @BindView(R.id.tv_gps_lat)
    TextView mGpsLat;
    @BindView(R.id.tv_gps_lng)
    TextView mGpsLng;
    @BindView(R.id.iv_direction_arrow)
    ImageView mDirectionArrow;
    @BindView(R.id.tv_distance)
    TextView mDistance;
    @BindView(R.id.fab_check_location)
    FloatingActionButton mCheckLocation;

    public RiddleDirectionsFragment() {
    }

    public static RiddleDirectionsFragment newInstance(Riddle riddle) {
        RiddleDirectionsFragment fragment = new RiddleDirectionsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRiddle = savedInstanceState.getParcelable(BUNDLE_RIDDLE);
        }
        View rootView = inflater.inflate(R.layout.fragment_riddle_directions, container, false);
        ButterKnife.bind(this, rootView);
        inflateView();
        return rootView;
    }

    private void inflateView() {
        mGpsLat.setText(String.valueOf(mRiddle.getGpsLat()));
        mGpsLng.setText(String.valueOf(mRiddle.getGpsLat()));
        if (!TextUtils.isEmpty(mRiddle.getImagePath())) {
            Picasso.get()
                    .load(mRiddle.getImagePath())
                    .into(mImage);
        }

        mCheckLocation.setOnClickListener(new OnCheckLocationFabClickListener());

        /**
         * Based on https://stackoverflow.com/questions/15083811/programmatically-rotate-drawable-or-view
         */
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);
        //TODO get correct rotation
        //TODO remember last position
        final RotateAnimation animRotate = new RotateAnimation(90.0f, -180.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(1500);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        mDirectionArrow.startAnimation(animSet);

        //TODO get correct distance
        mDistance.setText(String.valueOf(10).concat(getString(R.string.meters)));
    }

    public void setRiddle(Riddle riddle) {
        mRiddle = riddle;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(BUNDLE_RIDDLE, mRiddle);
        super.onSaveInstanceState(outState);
    }

    public interface OnCheckLocationClickListener {
        void onLocation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnCheckLocationClickListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCheckLocationClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    class OnCheckLocationFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnCheckLocationClickListener onClick");
            //TODO validate if is on position
            mCallback.onLocation();
        }
    }
}
