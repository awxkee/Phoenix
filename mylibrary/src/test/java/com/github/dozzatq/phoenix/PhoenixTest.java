package com.github.dozzatq.phoenix;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by dxfb on 03.09.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhoenixTest {

    @Mock
    Context mContext;

    @Before
    public void init()
    {
        Phoenix.getInstance().setContext(mContext);
    }

    @Test
    public void getUserId() throws Exception {
    }

    @Test
    public void getExposedUri() throws Exception {
    }

    @Test
    public void getExposedUri1() throws Exception {
    }

    @Test
    public void getPackageName() throws Exception {
    }

    @Test
    public void getContext() throws Exception {
    }

    @Test
    public void putString() throws Exception {
    }

    @Test
    public void putStringFuture() throws Exception {
        Phoenix phoenix = Phoenix.getInstance();
        phoenix.putStringFuture("key", "sad");
        assertSame(phoenix.getString("key"), "sad");
    }

    @Test
    public void putBoolean() throws Exception {
    }

    @Test
    public void putBooleanFuture() throws Exception {
    }

    @Test
    public void getBoolean() throws Exception {
    }

    @Test
    public void getLong() throws Exception {
    }

    @Test
    public void putFloat() throws Exception {
    }

    @Test
    public void putFloatFuture() throws Exception {
    }

}