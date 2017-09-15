package com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_sunshine);
        setPreferencesSummary(getPreferenceScreen());
    }

    private void setPreferencesSummary(PreferenceScreen preferenceScreen) {
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            setPreferenceSummary(preferenceScreen.getPreference(i));
        }
    }

    private void setPreferenceSummary(Preference preference) {
        if (preference instanceof EditTextPreference) {
            setEditTextPreferenceSummary((EditTextPreference) preference);
        } else if (preference instanceof ListPreference) {
            setListPreferenceSummary((ListPreference) preference);
        }
    }

    private void setEditTextPreferenceSummary(EditTextPreference preference) {
        preference.setSummary(preference.getText());
    }

    private void setListPreferenceSummary(ListPreference preference) {
        preference.setSummary(getLabelForValue(preference));
    }

    private CharSequence getLabelForValue(ListPreference preference) {
        int valueIndex = preference.findIndexOfValue(preference.getValue());
        if (valueIndex == -1) {
            return null;
        }
        return preference.getEntries()[valueIndex];
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            setPreferenceSummary(preference);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}
