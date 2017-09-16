/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DetailActivity.class.getSimpleName();

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private static final String HUMIDITY_UNITS = "%";
    private static final String PRESSURE_UNITS = "hPa";

    public static final String[] DETAILED_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
    };

    private static final int ID_DETAIL_FORECAST_LOADER = 55;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

    private Uri mUri;

    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mMaxTemperatureTextView;
    private TextView mMinTemperatureTextView;
    private TextView mHumidityTextView;
    private TextView mPressureTextView;
    private TextView mWindSpeedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDateTextView = (TextView) findViewById(R.id.tv_date);
        mDescriptionTextView = (TextView) findViewById(R.id.tv_description);
        mMaxTemperatureTextView = (TextView) findViewById(R.id.tv_max_temperature);
        mMinTemperatureTextView = (TextView) findViewById(R.id.tv_min_temperature);
        mHumidityTextView = (TextView) findViewById(R.id.tv_humidity);
        mPressureTextView = (TextView) findViewById(R.id.tv_pressure);
        mWindSpeedTextView = (TextView) findViewById(R.id.tv_wind_speed);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            Uri dataUri = intentThatStartedThisActivity.getData();
            if (dataUri == null) {
                throw new NullPointerException("Weather data URI is null.");
            }
            Log.d(TAG, "Weather data URI: " + dataUri);
            mUri = dataUri;
        }
        getSupportLoaderManager().initLoader(ID_DETAIL_FORECAST_LOADER, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_FORECAST_LOADER:
                return createDetailedForecastLoader();
            default:
                throw new UnsupportedOperationException("Unknown loader ID: " + id);
        }
    }

    private Loader<Cursor> createDetailedForecastLoader() {
        CursorLoader cursorLoader = new CursorLoader(this);
        cursorLoader.setUri(mUri);
        cursorLoader.setProjection(DETAILED_FORECAST_PROJECTION);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() != 1 || !data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        long date = data.getLong(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        double maxTemperature = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
        double minTemperature = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
        double humidity = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        double pressure = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        float windSpeed = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        float degrees = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));

        mDateTextView.setText(SunshineDateUtils.getFriendlyDateString(this, date, false));
        mDescriptionTextView.setText(SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId));
        mMaxTemperatureTextView.setText(SunshineWeatherUtils.formatTemperature(this, maxTemperature));
        mMinTemperatureTextView.setText(SunshineWeatherUtils.formatTemperature(this, minTemperature));
        mHumidityTextView.setText(Math.round(humidity) + " " + HUMIDITY_UNITS);
        mPressureTextView.setText(Math.round(pressure) + " " + PRESSURE_UNITS);
        mWindSpeedTextView.setText(SunshineWeatherUtils.getFormattedWind(this, windSpeed, degrees));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}