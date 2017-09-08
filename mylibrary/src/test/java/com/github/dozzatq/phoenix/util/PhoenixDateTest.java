package com.github.dozzatq.phoenix.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dxfb on 01.09.2017.
 */
public class PhoenixDateTest {
    @Test
    public void validate() throws Exception {
        assertTrue(PhoenixDate.validate("13/12/2001"));
        assertTrue(PhoenixDate.validate("13/12/2022"));
        assertTrue(PhoenixDate.validate("13/12/1900"));
        assertTrue(PhoenixDate.validate("12/12/1993"));
    }

}