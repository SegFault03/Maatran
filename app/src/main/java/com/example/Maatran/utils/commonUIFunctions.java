package com.example.Maatran.utils;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
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
            assert palette != null;
            int color = palette.getDominantColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimary));
            // Set the status bar color to the dominant color
            activity.getWindow().setStatusBarColor(color);
        });
    }

    default int changeLoadingDotAnimation(int currDot, AppCompatActivity activity)
    {
        int lastUpdatedDot = currDot;
        String currLoadDotView = "load_dot"+ lastUpdatedDot % 3;
        String oldLoadDotView = "load_dot"+ (lastUpdatedDot % 3 == 0 ? 2 : lastUpdatedDot % 3 - 1);
        lastUpdatedDot++;
        int currResID = activity.getResources().getIdentifier(currLoadDotView,"id",activity.getPackageName());
        int oldResID = activity.getResources().getIdentifier(oldLoadDotView,"id",activity.getPackageName());
        AppCompatImageView currResView = activity.findViewById(currResID);
        AppCompatImageView oldResView = activity.findViewById(oldResID);
        Drawable drawable = AppCompatResources.getDrawable(activity.getApplicationContext(),R.drawable.loading_dot_grey);
        drawable.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        currResView.setImageDrawable(drawable);
        oldResView.setImageResource(R.drawable.loading_dot_grey);
        return lastUpdatedDot;
    }
}
