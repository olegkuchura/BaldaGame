package com.adlab.balda.adapters;


import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adlab.balda.R;
import com.adlab.balda.contracts.BaseGamePresenter;
import com.adlab.balda.contracts.CellView;
import com.adlab.balda.enums.FieldSizeType;
import com.adlab.balda.utils.HexagonViewUtilsKt;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.adlab.balda.utils.HexagonUtils.hexagonCellCountBySize;

public class HexagonFieldRecyclerAdapter extends RecyclerView.Adapter<HexagonFieldRecyclerAdapter.RecyclerViewHolder>
        implements BaseFieldAdapter {

    private OnFieldItemClickListener listener;

    private BaseGamePresenter mPresenter;

    private ColorStateList defaultTextColor;

    private int mColumnCount;
    private int mRowCount;
    private FieldSizeType mFieldSize;
    private int mItemSizePx;
    private int mTextSize;
    private int mNumberSize;

    public HexagonFieldRecyclerAdapter(BaseGamePresenter presenter,
                                       int columnCount, FieldSizeType fieldSize, int itemSizePx, int textSize, int numberSize) {
        mPresenter = presenter;
        mColumnCount = columnCount;
        mFieldSize = fieldSize;
        mItemSizePx = itemSizePx;
        mTextSize = textSize;
        mNumberSize = numberSize;

        setHasStableIds(true);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hexagon_field_item, parent, false);

        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = mItemSizePx;
        params.width = HexagonViewUtilsKt.innerRadius(mItemSizePx);
        view.setLayoutParams(params);

        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        if (defaultTextColor == null) {
            defaultTextColor = holder.textViewLetter.getTextColors();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        mPresenter.bindCell(holder, position);

        GridLayoutManager.LayoutParams param = (GridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        param.setMargins(0 , 0, 0, 0);
        int leftMargin = HexagonViewUtilsKt.innerRadius(mItemSizePx) / 2;
        int topMargin = -(mItemSizePx / 4);

        int pos = position + 1;
        if (pos < mColumnCount + 1) {
            param.setMargins(0 , 0, 0, 0);
        } else if ((position - mColumnCount ) % (mColumnCount*2 - 1) == 0) {
            param.setMargins(leftMargin , topMargin , 0, 0);
        } else if ((position - mColumnCount ) % (mColumnCount*2 - 1) > 0 && (position - mColumnCount ) % (mColumnCount*2 - 1) < mColumnCount - 1) {
            param.setMargins((-leftMargin) , topMargin, 0, 0);
        } else {
            param.setMargins(0 , topMargin, 0, 0);
        }
        holder.itemView.setLayoutParams(param);
    }

    private void setTextColor(TextView view, @ColorRes int colorId) {
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
        return hexagonCellCountBySize(mFieldSize);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CellView {

        TextView textViewLetter;
        TextView textViewNumber;
        AppCompatImageView imageViewClearLetter;

        RecyclerViewHolder(View itemView) {
            super(itemView);

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
            textViewNumber.setText(null);
        }
    }
}

