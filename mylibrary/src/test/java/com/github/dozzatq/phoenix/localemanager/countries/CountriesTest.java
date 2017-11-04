package com.github.dozzatq.phoenix.localemanager.countries;

import android.util.Log;

import org.junit.Test;

/**
 * Created by Rodion Bartoshik on 08.09.2017.
 */
public class CountriesTest {
    private static final String TAG = CountriesTest.class.getName();

    @Test
    public void testLength() throws Exception {
        int length = Countries.COUNTRIES.length;
        Log.i(TAG, String.valueOf(length));
    }
}