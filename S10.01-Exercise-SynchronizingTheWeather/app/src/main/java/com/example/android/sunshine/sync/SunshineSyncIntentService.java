package com.example.android.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class SunshineSyncIntentService extends IntentService {

    private static final String TAG = SunshineSyncIntentService.class.getSimpleName();

    public SunshineSyncIntentService() {
        super(SunshineSyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Syncing weather data...");
        SunshineSyncTask.syncWeather(this);
    }
}