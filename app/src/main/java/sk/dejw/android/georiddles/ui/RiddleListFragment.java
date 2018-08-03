package sk.dejw.android.georiddles.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.adapters.RiddleAdapter;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleListFragment extends Fragment implements RiddleAdapter.RiddleAdapterOnClickHandler {
    private static final String TAG = RiddleListFragment.class.getSimpleName();

    private static final String BUNDLE_RIDDLES = "riddles";

    @BindView(R.id.rv_riddles_list)
    RecyclerView mRiddlesRecyclerView;

    private ArrayList<Riddle> mRiddles;
    private RiddleAdapter mRiddleAdapter;
    private OnRiddleClickListener mCallback;

    public interface OnRiddleClickListener {
        void onRiddleSelected(int riddleId);
    }

    public RiddleListFragment() {
    }

    public static RiddleListFragment newInstance(ArrayList<Riddle> riddles) {
        Log.d(TAG, "newInstance");

        RiddleListFragment fragment = new RiddleListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BUNDLE_RIDDLES, riddles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRiddles = getArguments().getParcelableArrayList(BUNDLE_RIDDLES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if (mRiddles == null && savedInstanceState != null) {
            mRiddles = savedInstanceState.getParcelableArrayList(BUNDLE_RIDDLES);
        }

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_riddle_list, container, false);
        ButterKnife.bind(this, rootView);

        mRiddlesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRiddleAdapter = new RiddleAdapter(getActivity(), mRiddles, this);
        mRiddlesRecyclerView.setAdapter(mRiddleAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);

        try {
            mCallback = (OnRiddleClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRiddleClickListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");

        outState.putParcelableArrayList(BUNDLE_RIDDLES, mRiddles);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRiddleClick(int riddleId) {
        Log.d(TAG, "onRiddleClick");

        mCallback.onRiddleSelected(riddleId);
    }
}
