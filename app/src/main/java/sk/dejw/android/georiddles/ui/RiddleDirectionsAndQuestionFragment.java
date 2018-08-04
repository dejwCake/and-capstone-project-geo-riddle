package sk.dejw.android.georiddles.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;
import sk.dejw.android.georiddles.providers.RiddleContract;
import sk.dejw.android.georiddles.utils.cursor.RiddleCursorUtils;

public class RiddleDirectionsAndQuestionFragment extends Fragment {
    private static final String TAG = RiddleDirectionsAndQuestionFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";
    public static final int DEFAULT_DISTANCE = 99999999;
    public static final int DISTANCE_THRESHOLD = 20;

    @BindView(R.id.iv_image)
    ImageView mImage;
    @BindView(R.id.tv_gps_lat)
    TextView mGpsLat;
    @BindView(R.id.tv_gps_lng)
    TextView mGpsLng;
    @BindView(R.id.iv_direction_arrow)
    ImageView mDirectionArrow;
    @BindView(R.id.tv_distance)
    TextView mDistanceText;
    @BindView(R.id.fab_check_location)
    FloatingActionButton mCheckLocation;
    @BindView(R.id.tv_question)
    TextView mQuestion;
    @BindView(R.id.et_answer)
    EditText mAnswer;
    @BindView(R.id.bt_check)
    Button mCheckAnswerButton;

    @BindView(R.id.tv_correct_answer)
    TextView mCorrectAnswer;
    @BindView(R.id.bt_next)
    Button mNextButton;

    @BindView(R.id.riddle_directions)
    CoordinatorLayout mRiddleDirections;
    @BindView(R.id.riddle_question)
    ScrollView mRiddleQuestion;

    private Riddle mRiddle;
    private Riddle mNextRiddle;
    private Location mUserLocation;
    private float mLastBearing = 0;
    private float mCurrentBearing = 0;
    private float mDistance = DEFAULT_DISTANCE;

    public OnCorrectLocationListener mCorrectLocationCallback;
    public OnCorrectAnswerListener mCorrectAnswerCallback;

    public interface OnCorrectLocationListener {
        void onCorrectLocation();
    }

    public interface OnCorrectAnswerListener {
        void onCorrectAnswer(Riddle nextRiddle);
    }

    public RiddleDirectionsAndQuestionFragment() {
    }

    public static RiddleDirectionsAndQuestionFragment newInstance(Riddle riddle) {
        Log.d(TAG, "newInstance");

        RiddleDirectionsAndQuestionFragment fragment = new RiddleDirectionsAndQuestionFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (mRiddle == null && savedInstanceState != null) {
            mRiddle = savedInstanceState.getParcelable(BUNDLE_RIDDLE);
        }
        if (mRiddle != null) {
            mNextRiddle = getNextRiddle();
        }

        View rootView = inflater.inflate(R.layout.fragment_riddle_directions_and_question, container, false);
        ButterKnife.bind(this, rootView);

        Log.d(TAG, "Riddle: " + mRiddle.getTitle());

        updateUi();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);

        try {
            mCorrectLocationCallback = (OnCorrectLocationListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCorrectLocationListener");
        }

        try {
            mCorrectAnswerCallback = (OnCorrectAnswerListener) getParentFragment();
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

    public void setRiddle(Riddle riddle) {
        Log.d(TAG, "setRiddle");

        mRiddle = riddle;
    }

    public void setUserLocation(Location userLocation) {
        Log.d(TAG, "setUserLocation");

        mUserLocation = userLocation;
    }

    public void updateUi() {
        Log.d(TAG, "updateUi");

        if (mRiddle == null) {
            return;
        }

        Location riddleLocation = mRiddle.getLocation();

        //Static UI
        mGpsLat.setText(String.valueOf(mRiddle.getGpsLat()));
        mGpsLng.setText(String.valueOf(mRiddle.getGpsLng()));
        if (!TextUtils.isEmpty(mRiddle.getImagePath())) {
            Picasso.get()
                    .load(mRiddle.getImagePath())
                    .into(mImage);
        }

        mCheckLocation.setOnClickListener(new OnCheckLocationFabClickListener());
        mCheckAnswerButton.setOnClickListener(new OnCheckAnswerClickListener());
        mNextButton.setOnClickListener(new OnMoveToNextRiddleClickListener());

        mQuestion.setText(mRiddle.getQuestion());

        //Dynamic UI
        /**
         * Based on https://stackoverflow.com/questions/15083811/programmatically-rotate-drawable-or-view
         */
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);
        mLastBearing = mCurrentBearing;
        if (mUserLocation != null) {
            mCurrentBearing = mUserLocation.bearingTo(riddleLocation);
        }
        final RotateAnimation animRotate = new RotateAnimation(mLastBearing, mCurrentBearing,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(1500);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        mDirectionArrow.startAnimation(animSet);

        if (mUserLocation != null) {
            mDistance = mUserLocation.distanceTo(riddleLocation);
            Log.d(TAG, "Distance between: " + mUserLocation.toString() + " and " + mRiddle.getLocation().toString() + " is: " + mDistance);
        }
        if (mDistance <= DISTANCE_THRESHOLD) {
            mCheckLocation.setEnabled(true);
            mCheckLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_black_24dp));
            mCheckLocation.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            mCheckLocation.setEnabled(false);
            mCheckLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_off_black_24dp));
            mCheckLocation.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentTint)));
        }
        String distanceString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format((int) mDistance);
        mDistanceText.setText(distanceString.concat(getString(R.string.meters)));

        if (!mRiddle.isLocationChecked() && !mRiddle.isRiddleSolved()) {
            mRiddleDirections.setVisibility(View.VISIBLE);
            mRiddleQuestion.setVisibility(View.GONE);
        } else if (mRiddle.isLocationChecked() && !mRiddle.isRiddleSolved()) {
            mRiddleDirections.setVisibility(View.GONE);
            mRiddleQuestion.setVisibility(View.VISIBLE);

            mAnswer.setVisibility(View.VISIBLE);
            mCheckAnswerButton.setVisibility(View.VISIBLE);
            mCorrectAnswer.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
        } else {
            mRiddleDirections.setVisibility(View.GONE);
            mRiddleQuestion.setVisibility(View.VISIBLE);

            mAnswer.setVisibility(View.GONE);
            mCheckAnswerButton.setVisibility(View.GONE);
            mCorrectAnswer.setVisibility(View.VISIBLE);
            mCorrectAnswer.setText(getString(R.string.correct_answer).concat(" ").concat(mRiddle.getAnswer()));
            if (mNextRiddle != null) {
                mNextButton.setText(getString(R.string.next_riddle));
            } else {
                mNextButton.setText(getString(R.string.finish));
            }
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    private Riddle getNextRiddle() {
        String selection = RiddleContract.Entry.COLUMN_NO + " = ?";
        String[] selectionArgs = {String.valueOf(mRiddle.getNo() + 1)};
        Cursor nextRiddleCursor = getActivity().getContentResolver().query(
                RiddleContract.Entry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                RiddleContract.Entry.COLUMN_NO);
        return RiddleCursorUtils.getFirstRiddleFromCursor(nextRiddleCursor);
    }

    private void showCorrectAnswerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.correct_answer_dialog_title);
        if (mNextRiddle == null) {
            builder.setMessage(R.string.correct_answer_dialog_text_final)
                    .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mCorrectAnswerCallback.onCorrectAnswer(mNextRiddle);
                        }
                    });
        } else {
            builder.setMessage(R.string.correct_answer_dialog_text)
                    .setPositiveButton(R.string.next_riddle, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mCorrectAnswerCallback.onCorrectAnswer(mNextRiddle);
                        }
                    });
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class OnCheckLocationFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnCorrectLocationListener onClick");

            if (mDistance < DISTANCE_THRESHOLD) {
                mCorrectLocationCallback.onCorrectLocation();
            } else {
                Toast.makeText(getContext(), getString(R.string.not_in_correct_location), Toast.LENGTH_LONG).show();
            }
        }
    }

    class OnCheckAnswerClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnCorrectAnswerListener onClick");

            if (mAnswer.getText().toString().equals(mRiddle.getAnswer())) {
                showCorrectAnswerDialog();
            } else {
                Toast.makeText(getContext(), getString(R.string.incorrect_answer), Toast.LENGTH_LONG).show();
            }
        }
    }

    class OnMoveToNextRiddleClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "OnCorrectAnswerListener onClick");

            mCorrectAnswerCallback.onCorrectAnswer(mNextRiddle);
        }
    }
}
