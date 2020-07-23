package com.adlab.balda.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchOnlyVisibleFrameLayout extends FrameLayout {

    public TouchOnlyVisibleFrameLayout(@NonNull Context context) {
        super(context);
        setDrawingCacheEnabled(true);
    }

    public TouchOnlyVisibleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
    }

    public TouchOnlyVisibleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDrawingCacheEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (isPixelTransparent(x, y))
                return false;
            else
                return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    /**
     * @return true if pixel from (x,y) is transparent
     */
    private boolean isPixelTransparent(int x, int y) {
        Bitmap bmp = Bitmap.createBitmap(getDrawingCache());
        int color = Color.TRANSPARENT;
        try {
            color = bmp.getPixel(x, y);
        } catch (IllegalArgumentException e) {
            // x or y exceed the bitmap's bounds.
            // Reverts the View's internal state from a previously set "pressed" state.
            setPressed(false);
        }

        return color == Color.TRANSPARENT;
    }
}
