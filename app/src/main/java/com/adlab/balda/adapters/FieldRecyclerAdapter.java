package com.adlab.balda.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adlab.balda.R;
import com.adlab.balda.activities.Field;
import com.adlab.balda.contracts.GameContract;
import com.adlab.balda.presenters.GamePresenter;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Oleg on 07.08.2018.
 */

public class FieldRecyclerAdapter extends RecyclerView.Adapter<FieldRecyclerAdapter.RecyclerViewHolder> {

    private final int itemSize = 65;
    private final int textSize = 48;

    private OnItemClickListener listener;

    private GameContract.Presenter mPresenter;

    private ColorStateList defaultTextColor;

    private int mFieldSize;

    public FieldRecyclerAdapter(GameContract.Presenter presenter, int fieldSize) {
        mPresenter = presenter;
        mFieldSize = fieldSize;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_item, parent, false);

        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemSize, parent.getContext().getResources().getDisplayMetrics());
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = (int) pixels;
        params.width = (int) pixels;
        view.setLayoutParams(params);

        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        if (defaultTextColor == null){
            defaultTextColor = holder.textViewLetter.getTextColors();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        mPresenter.bindCell(holder, position);
    }

    private void setTextColor(TextView view, @ColorRes int colorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextColor(view.getContext().getColor(colorId));
        } else {
            view.setTextColor(view.getContext().getResources().getColor(colorId));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFieldSize;
    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, GameContract.CellView {

        TextView textViewLetter;
        TextView textViewNumber;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            textViewLetter = itemView.findViewById(R.id.field_item_text_view);
            textViewNumber = itemView.findViewById(R.id.tv_number);
            textViewLetter.setTextSize(textSize);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(itemView, position);
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void showLetter(char letter) {
            textViewLetter.setText(String.valueOf(letter).toUpperCase());
            setTextColor(textViewLetter, R.color.colorAccent);
        }

        @Override
        public void showEnteredLetter(char letter) {
            textViewLetter.setText(String.valueOf(letter).toUpperCase());
            textViewLetter.setTextColor(defaultTextColor);
        }

        @Override
        public void select() {
            itemView.setActivated(false);
            textViewNumber.setText(null);
            itemView.setSelected(true);
        }

        @Override
        public void activate(int number) {
            itemView.setSelected(false);
            itemView.setActivated(true);
            textViewNumber.setText(String.valueOf(number));
        }

        @Override
        public void resetState() {
            itemView.setSelected(false);
            itemView.setActivated(false);
            textViewNumber.setText(null);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onItemLongClick(View itemView, int position);
    }
}
