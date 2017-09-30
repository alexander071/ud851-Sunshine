/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.sunshine.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class SunshineFirebaseJobService extends JobService {

    private static final String TAG = SunshineFirebaseJobService.class.getSimpleName();

    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.d(TAG, "Executing SunshineFirebaseJobService.onStartJob...");
        mFetchWeatherTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void[] parameters) {
                SunshineSyncTask.syncWeather(SunshineFirebaseJobService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                jobFinished(job, false);
            }

        }.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}
