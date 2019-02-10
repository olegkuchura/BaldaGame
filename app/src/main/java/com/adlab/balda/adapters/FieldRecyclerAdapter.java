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

    private Field field;
    private ColorStateList defaultTextColor;

    private Context context;

    public FieldRecyclerAdapter(Context context, Field field) {
        this.field = field;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context){
        this.context = context;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.field_item, parent, false);

        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemSize, context.getResources().getDisplayMetrics());
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = (int) pixels;
        params.width = (int) pixels;
        view.setLayoutParams(params);

        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        if (defaultTextColor == null){
            defaultTextColor = holder.textView.getTextColors();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (position == field.getEnteredViewNumber()) {
            holder.textView.setText(String.valueOf(field.getEnteredLetter()).toUpperCase());
            holder.textView.setTextColor(defaultTextColor);
        } else {
            String letter = String.valueOf(field.getLetter(position)).toUpperCase();
            holder.textView.setText(letter);
            setTextColor(holder.textView, R.color.colorAccent);
        }
        int activeCellNumber = field.getActiveCellNumber(position);
        if(activeCellNumber != -1){
            holder.itemView.setActivated(true);
            holder.textViewNumber.setText(String.valueOf(activeCellNumber + 1));
        } else {
            holder.itemView.setActivated(false);
            holder.textViewNumber.setText("");
        }
        holder.itemView.setTag(position);
        if (position == field.getSelectedViewNumber()) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    private void setBackgroundColor(View view, int colorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(context.getColor(colorId));
        } else {
            view.setBackgroundColor(context.getResources().getColor(colorId));
        }
    }

    private void setTextColor(TextView view, @ColorRes int colorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextColor(context.getColor(colorId));
        } else {
            view.setTextColor(context.getResources().getColor(colorId));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return field.getCellCount();
    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView textView;
        TextView textViewNumber;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.field_item_text_view);
            textViewNumber = itemView.findViewById(R.id.tv_number);
            textView.setTextSize(textSize);

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
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onItemLongClick(View itemView, int position);
    }
}
