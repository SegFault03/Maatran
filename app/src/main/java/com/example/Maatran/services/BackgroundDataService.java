package com.example.Maatran.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class BackgroundDataService extends IntentService {
    /**
     * @param name
     * @deprecated
     */
    public BackgroundDataService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
