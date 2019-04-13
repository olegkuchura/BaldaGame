package com.adlab.balda.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlockTouchEventLayout extends FrameLayout {

    private TouchListener listener;

    public BlockTouchEventLayout(@NonNull Context context) {
        super(context);
    }

    public BlockTouchEventLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockTouchEventLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (listener != null) {
            listener.onTouch(ev);
        }
        return true;
    }

    public void setTouchListener(TouchListener listener) {
        this.listener = listener;
    }

    public interface TouchListener {
        void onTouch(MotionEvent ev);
    }
}
