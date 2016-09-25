package com.deity.wxredpackets.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.deity.wxredpackets.R;

/**
 * Created by Deity on 2016/9/20.
 */
public class GeneralSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_config);
    }
}
