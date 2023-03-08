package com.example.Maatran.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.example.Maatran.R;

public interface commonUIFunctions {
    default void changeStatusBarColor(Drawable backgroundDrawable, AppCompatActivity activity)
    {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Palette.from(bitmap).generate(palette -> {
            // Get the dominant color from the Palette
            int color = palette.getDominantColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary));
            // Set the status bar color to the dominant color
            activity.getWindow().setStatusBarColor(color);
        });
    }
}
