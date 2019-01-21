package com.samsung.myitschool.codegenerator;

import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}