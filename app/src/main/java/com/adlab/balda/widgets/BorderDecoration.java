package com.adlab.balda.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BorderDecoration extends RecyclerView.ItemDecoration {

    private final Paint mPaint;
    private int mSpanCount;

    /**
     * Create a decoration that draws a line in the given color and width between the items in the grid.
     *
     * @param context  a context to access the resources.
     * @param color    the color of the separator to draw.
     * @param spacingDp the thickness of the separator in dp.
     */
    public BorderDecoration(@NonNull Context context, @ColorInt int color,
                            @FloatRange(from = 0, fromInclusive = false) float spacingDp, int spanCount) {
        mPaint = new Paint();
        mPaint.setColor(color);
        final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                spacingDp, context.getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(thickness);
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        final int position = params.getViewAdapterPosition();

        if (position < state.getItemCount()) {
            int left, top, right, bottom;
            int spacing = (int) mPaint.getStrokeWidth();
            int columnNumber = position % mSpanCount;

            if (position < mSpanCount ) {
                top = spacing;
            } else {
                top = 0;
            }

            left = spacing - columnNumber * spacing / mSpanCount;
            right = (columnNumber + 1) * spacing / mSpanCount;

            bottom = spacing;

            outRect.set(left, top, right, bottom);
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final float offset = mPaint.getStrokeWidth() / 2;

        for (int i = 0; i < parent.getChildCount(); i++) {
            final View view = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            final int position = params.getViewAdapterPosition();

            if (position < state.getItemCount()) {
                // top line
                c.drawLine(view.getLeft(), view.getTop() - offset, view.getRight() + offset * 2, view.getTop() - offset, mPaint);

                // right line
                c.drawLine(view.getRight() + offset, view.getTop() , view.getRight() + offset, view.getBottom() + offset * 2, mPaint);

                // bottom line
                c.drawLine(view.getLeft() - offset * 2, view.getBottom() + offset, view.getRight(), view.getBottom() + offset, mPaint);

                // left line
                c.drawLine(view.getLeft() - offset , view.getTop() - offset * 2 , view.getLeft() - offset , view.getBottom() , mPaint);
            }
        }
    }

}
