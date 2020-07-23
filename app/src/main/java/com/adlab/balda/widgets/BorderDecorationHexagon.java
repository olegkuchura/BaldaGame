package com.adlab.balda.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.adlab.balda.R;
import com.adlab.balda.utils.ViewUtilsKt;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.adlab.balda.utils.HexagonUtils.coeffByColumnCount;

public class BorderDecorationHexagon extends RecyclerView.ItemDecoration {

    private final Paint mPaint;
    private int mSpanCount;
    private float mSpacingDp;
    private float mSpacingPx;

    /**
     * Create a decoration that draws a line in the given color and width between the items in the grid.
     *
     * @param context  a context to access the resources.
     * @param spacingDp the thickness of the separator in dp.
     */
    public BorderDecorationHexagon(@NonNull Context context,
                            @FloatRange(from = 0, fromInclusive = false) float spacingDp, int spanCount) {
        mSpacingDp = spacingDp;
        mPaint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPaint.setColor(context.getResources().getColor(R.color.border, context.getTheme()));
        } else {
            mPaint.setColor(context.getResources().getColor(R.color.border));
        }
        mSpacingPx = ViewUtilsKt.dpToPx(context, spacingDp);
        mPaint.setStrokeWidth((int)(mSpacingPx * 2));
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        final int position = params.getViewAdapterPosition();
        //if (true) return;
        if (position < state.getItemCount()) {
            int left = 0, top = 0, right = 0, bottom = 0;
            //int spacing = (int) mPaint.getStrokeWidth();
            int spacing = (int) mSpacingPx;
            int b = position % (mSpanCount * 2 - 1);
            //int columnNumber = position % mSpanCount;
            int columnNumber;
            if (b < mSpanCount) columnNumber = b;
            else {
                columnNumber = b - mSpanCount;
            }

            if (position < mSpanCount ) {
                top = spacing;
            } else {
                top = 0;
            }
            left = (int)(spacing - columnNumber * spacing / (float)mSpanCount);
            right = (int)((columnNumber + 1) * spacing / (float)mSpanCount);

            if (position % (mSpanCount * 2 - 1) == 0) {
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) < mSpanCount) {
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount) {
                //outRect.set((int)((float)spacing * 1.5F), top, spacing, spacing);
                left = ((int)(spacing * 1.5));
                right = (columnNumber + 1) * spacing / mSpanCount;
                Log.d("BALDADEV", "spacing= " + spacing);
                Log.d("BALDADEV", "11111p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            /*if (position % (mSpanCount * 2 - 1) == mSpanCount + 1) {
                left = 3; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 2) {
                left = 0; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 3) {
                left = 3; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 4) {
                left = 0; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 5) {
                left = -3; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 6) {
                left = -6; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }
            if (position % (mSpanCount * 2 - 1) == mSpanCount + 7) {
                left = -9; right = 0;
                Log.d("BALDADEV", "2222p= " + position + " left = " + left + "  right = " + right);
                outRect.set(left, top, right, spacing);
                return;
            }*/

            if (position % (mSpanCount * 2 - 1) > mSpanCount) {
                int s = spacing / 3;
                int k = (mSpanCount - 2) / 2;
                if (k == 0) {
                    left = 0;
                } else {
                    int e = s / k;
                    //left = s - ((columnNumber - 1) * e);
                    left = ((mSpanCount / 2) - columnNumber) * e;
                }
                left = (int)(spacing - columnNumber * spacing / (float)mSpanCount );
                right = (int)((columnNumber + 1) * spacing / (float)mSpanCount);
                Log.d("BALDADEV", "p= " + position + " left = " + left + "  right = " + 0);
                left = left - (int)(coeffByColumnCount(mSpanCount) * mSpacingDp);
                Log.d("BALDADEV", "p= " + position + " left = " + left + "  right = " + 0);
                outRect.set(left , top, 0 , spacing);
                return;
            }

            outRect.set(left, top, right, bottom);
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //if (true) return;
        //mPaint.setStrokeWidth(mPaint.getStrokeWidth()*5);
        //final float offset = mPaint.getStrokeWidth() / 2;
        float offset = mSpacingPx;
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View view = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            final int position = params.getViewAdapterPosition();

            final float halfOuterRadius = (view.getBottom() - view.getTop()) / 4.0f;
            final float halfW = (view.getRight() - view.getLeft()) / 2.0f;

            if (position < state.getItemCount()) {
                // left line
                c.drawLine(view.getLeft(), view.getBottom() - halfOuterRadius + offset/2, view.getLeft() , view.getTop() - offset/2 + halfOuterRadius, mPaint);

                // left-top line
                c.drawLine(view.getLeft() - offset/2 , view.getTop() + halfOuterRadius + offset/4, view.getRight() - halfW + offset/2 , view.getTop() - offset/4  , mPaint);

                // top-right line
                c.drawLine(view.getRight() - halfW - offset/2, view.getTop() - offset/4, view.getRight() + offset/2 , view.getTop() + offset/4 + halfOuterRadius, mPaint);

                // right line
                c.drawLine(view.getRight() , view.getTop() - offset/2 + halfOuterRadius, view.getRight(), view.getTop() + offset/2 + halfOuterRadius * 3, mPaint);

                // right-bottom line
                c.drawLine(view.getRight() + offset/2 , view.getTop() - offset/4 + halfOuterRadius * 3, view.getRight() - offset/2 -  halfW, view.getBottom() + offset/4, mPaint);

                // left-bottom line
                c.drawLine(view.getRight() + offset/2 -  halfW, view.getBottom() + offset/4, view.getLeft() - offset/2, view.getTop() - offset/4 + halfOuterRadius * 3, mPaint);
            }
        }
    }
}
