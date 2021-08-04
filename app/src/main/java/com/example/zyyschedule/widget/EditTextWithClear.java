package com.example.zyyschedule.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;


import com.example.zyyschedule.R;



public class EditTextWithClear extends AppCompatEditText {
    private final Context mcontext;
    private final Drawable cleardrawable;

    public EditTextWithClear(@NonNull Context context) {
        super(context);
        mcontext = context;
        cleardrawable = ContextCompat.getDrawable(mcontext, R.drawable.ic_baseline_clear_24);
    }

    public EditTextWithClear(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        cleardrawable = ContextCompat.getDrawable(mcontext, R.drawable.ic_baseline_clear_24);
    }

    public EditTextWithClear(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        cleardrawable = ContextCompat.getDrawable(mcontext, R.drawable.ic_baseline_clear_24);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        toggleClearIcon(text);
    }

    private void toggleClearIcon(CharSequence text) {
        if (text.length()>0) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, cleardrawable, null);
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            if (cleardrawable != null) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > getWidth()-cleardrawable.getIntrinsicWidth()-20
                        && event.getX() < getWidth()+20
                        && event.getY() > getHeight() / 2 - cleardrawable.getIntrinsicHeight() / 2-20
                        && event.getY() < getHeight() / 2 + cleardrawable.getIntrinsicHeight() / 2+20){
                    setText("");
                }
            }
        }
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


}
