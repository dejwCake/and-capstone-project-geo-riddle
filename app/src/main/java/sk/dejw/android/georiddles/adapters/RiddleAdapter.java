package sk.dejw.android.georiddles.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.dejw.android.georiddles.R;
import sk.dejw.android.georiddles.models.Riddle;

public class RiddleAdapter extends RecyclerView.Adapter<RiddleAdapter.RiddleViewHolder> {

    private Context mContext;
    private ArrayList<Riddle> mData;
    private RiddleAdapterOnClickHandler mClickHandler;

    public RiddleAdapter(Context context, ArrayList<Riddle> data, RiddleAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mData = data;
        this.mClickHandler = clickHandler;
    }

    public interface RiddleAdapterOnClickHandler {
        void onRiddleClick(long riddlePosition);
    }

    @Override
    public RiddleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.riddle_list_item, parent, false);

        return new RiddleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RiddleViewHolder holder, int position) {
        Riddle riddle = mData.get(position);

        holder.riddleNoTextView.setText(String.valueOf(riddle.getNo()).concat("."));
        holder.riddleTitleTextView.setText(riddle.getTitle());
        holder.itemPosition = position;
        holder.riddle = riddle;
        if (riddle.isActive() || riddle.isRiddleSolved()) {
            holder.riddleItemConstraintLayout.setEnabled(true);
            if(riddle.isActive()) {
                if(!riddle.isLocationChecked() && !riddle.isRiddleSolved()) {
                    holder.riddleStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_location_off_black_24dp));
                } else if(riddle.isLocationChecked() && !riddle.isRiddleSolved()) {
                    holder.riddleStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_location_on_black_24dp));
                } else {
                    holder.riddleStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_green_24dp));
                }
            } else {
                holder.riddleStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_green_24dp));
            }
        } else {
            holder.riddleItemConstraintLayout.setEnabled(false);
            holder.riddleNoTextView.setTextColor(mContext.getResources().getColor(R.color.colorDisabled));
            holder.riddleTitleTextView.setTextColor(mContext.getResources().getColor(R.color.colorDisabled));
            holder.riddleStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close_red_24dp));
        }

    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    public class RiddleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_riddle_no)
        TextView riddleNoTextView;
        @BindView(R.id.tv_riddle_title)
        TextView riddleTitleTextView;
        @BindView(R.id.iv_riddle_status)
        ImageView riddleStatus;
        @BindView(R.id.cl_riddle_item)
        ConstraintLayout riddleItemConstraintLayout;
        Riddle riddle;
        int itemPosition;

        public RiddleViewHolder(View layoutView) {
            super(layoutView);
            ButterKnife.bind(this, layoutView);

            layoutView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickHandler.onRiddleClick(riddle.getId());
                }
            });
        }
    }
}
