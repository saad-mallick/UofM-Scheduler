package saadandaakash.uofmscheduler.Fragments;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;

import java.util.Map;

import saadandaakash.uofmscheduler.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences preferences;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        preferences = getPreferenceManager().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> preferencesMap = preferences.getAll();
        // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            if (preferenceEntry instanceof EditTextPreference) {
                updateSummary((EditTextPreference) preferenceEntry);
            }
        }
    }

    @Override
    public void onPause() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Map<String, ?> preferencesMap = sharedPreferences.getAll();

        // get the preference that has been changed
        Object changedPreference = preferencesMap.get(key);

        // and if it's an instance of EditTextPreference class, update its summary
        if (preferencesMap.get(key) instanceof EditTextPreference) {
            updateSummary((EditTextPreference) changedPreference);
        }
    }

    private void updateSummary(EditTextPreference preference) {
        // set the EditTextPreference's summary value to its current text
        preference.setSummary(preference.getText());
    }
}
