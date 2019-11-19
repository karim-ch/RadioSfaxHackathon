package com.RadioSfax.acer.radiosfax;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.button.MaterialButton;
import android.util.AttributeSet;

public class CustomButton extends MaterialButton {

    public CustomButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        setTypeface(face);
    }
}
