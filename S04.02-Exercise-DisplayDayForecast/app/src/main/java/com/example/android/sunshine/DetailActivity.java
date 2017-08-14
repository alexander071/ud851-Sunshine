package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private TextView mDisplayWeatherForDayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDisplayWeatherForDayTextView = (TextView) findViewById(R.id.tv_display_weather_for_day);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String weatherForDay = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            mDisplayWeatherForDayTextView.setText(weatherForDay);
        }
    }
}