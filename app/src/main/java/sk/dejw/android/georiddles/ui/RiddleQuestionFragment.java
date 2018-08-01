package sk.dejw.android.georiddles.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleQuestionFragment extends Fragment {
    private static final String TAG = RiddleQuestionFragment.class.getSimpleName();

    public static final String BUNDLE_RIDDLE = "riddle";

    private Riddle mRiddle;

    @BindView(R.id.tv_question)
    TextView mQuestion;
    @BindView(R.id.et_answer)
    EditText mAnswer;


    public RiddleQuestionFragment() {
    }

    public static RiddleQuestionFragment newInstance(Riddle riddle) {
        RiddleQuestionFragment fragment = new RiddleQuestionFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_riddle_question, container, false);
        ButterKnife.bind(this, rootView);
        inflateView();
        return rootView;
    }

    private void inflateView() {
        mQuestion.setText(mRiddle.getQuestion());
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
