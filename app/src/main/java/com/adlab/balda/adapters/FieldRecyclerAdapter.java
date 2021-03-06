package com.adlab.balda.adapters;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adlab.balda.R;
import com.adlab.balda.contracts.BaseGamePresenter;
import com.adlab.balda.contracts.CellView;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.widgets.FieldCellView;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Oleg on 07.08.2018.
 */

public class FieldRecyclerAdapter extends RecyclerView.Adapter<FieldRecyclerAdapter.RecyclerViewHolder>
        implements BaseFieldAdapter {

    private OnFieldItemClickListener listener;

    private BaseGamePresenter mPresenter;

    private ColorStateList defaultTextColor;

    private FieldSizeType mFieldSize;
    private int mItemSizePx;
    private int mTextSize;
    private int mNumberSize;

    public FieldRecyclerAdapter(BaseGamePresenter presenter, FieldSizeType fieldSize, int itemSizePx, int textSize, int numberSize) {
        mPresenter = presenter;
        mFieldSize = fieldSize;
        mItemSizePx = itemSizePx;
        mTextSize = textSize;
        mNumberSize = numberSize;
    }

    @Override
    public void setOnItemClickListener(@NotNull OnFieldItemClickListener listener) {
        this.listener = listener;
    }

    public void setSizeOfCell(int itemSizePx, int textSize, int numberSize) {
        mItemSizePx = itemSizePx;
        mTextSize = textSize;
        mNumberSize = numberSize;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_item, parent, false);

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = mItemSizePx;
        params.width = mItemSizePx;
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
        return mFieldSize.intValue() * mFieldSize.intValue();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CellView {

        TextView textViewLetter;
        TextView textViewNumber;
        AppCompatImageView imageViewClearLetter;
        FieldCellView fieldCell;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            fieldCell = (FieldCellView) itemView;
            textViewLetter = itemView.findViewById(R.id.field_item_text_view);
            textViewNumber = itemView.findViewById(R.id.tv_number);
            imageViewClearLetter = itemView.findViewById(R.id.iv_clear_letter);
            textViewLetter.setTextSize(mTextSize);
            textViewNumber.setTextSize(mNumberSize);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imageViewClearLetter.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                if (view.getId() == R.id.iv_clear_letter) {
                    listener.onClearLetterClick();
                } else {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
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
        public void showClearLetterButton() {
            imageViewClearLetter.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideClearLetterButton() {
            imageViewClearLetter.setVisibility(View.GONE);
        }

        @Override
        public void resetState() {
            itemView.setSelected(false);
            itemView.setActivated(false);
            fieldCell.setShowingMove(false);
            textViewNumber.setText(null);
        }

        @Override
        public void moveSelect(int number) {
            fieldCell.setShowingMove(true);
            textViewNumber.setText(String.valueOf(number));
        }
    }
}
