package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask {

    public synchronized static void syncWeather(Context context) {
        ContentValues[] freshWeatherData = fetchFreshWeatherData(context);
        if (freshWeatherData != null && freshWeatherData.length != 0) {
            updateWeatherDataInDatabase(context, freshWeatherData);
        }
    }

    private static ContentValues[] fetchFreshWeatherData(Context context) {
        URL weatherRequestUrl = NetworkUtils.getUrl(context);
        try {
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            return OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonWeatherResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateWeatherDataInDatabase(Context context, ContentValues[] freshWeatherData) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
        contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, freshWeatherData);
    }
}