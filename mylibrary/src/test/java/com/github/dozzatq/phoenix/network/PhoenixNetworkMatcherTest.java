package com.github.dozzatq.phoenix.network;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

/**
 * Created by dxfb on 01.09.2017.
 */

public class PhoenixNetworkMatcherTest {
    @Test
    public void isValidEmail() throws Exception {
        assertTrue(PhoenixNetworkMatcher.isValidEmail("fdsfsd@gmail.com"));
        assertTrue(PhoenixNetworkMatcher.isValidEmail("hg@rewter.3om"));
        assertTrue(PhoenixNetworkMatcher.isValidEmail("hgdsgdst4t34qt543@rewte.r3om"));
        assertTrue(PhoenixNetworkMatcher.isValidEmail("jhfdj6eyu45ewte@r.coom"));
        assertTrue(PhoenixNetworkMatcher.isValidEmail("jhfdj6eyu45ewt@hgdyujy5eu6jhgfj.5e6u56ejffhfjf"));
    }

    @Test
    public void isValidWebURL() throws Exception {
        assertTrue(PhoenixNetworkMatcher.isValidWebURL("https://fdsfdsarf.com"));
        assertTrue(PhoenixNetworkMatcher.isValidWebURL("https://gfdsbvbfvds.hdhjewlkyj5.ru"));
        assertTrue(PhoenixNetworkMatcher.isValidWebURL("http://пидарыточкаком.ry"));
        assertTrue(PhoenixNetworkMatcher.isValidWebURL("http://пидарыточкаком.ru"));
        assertTrue(PhoenixNetworkMatcher.isValidWebURL("http://пидарыточкаком.fds"));
    }

    @Test
    public void isValidDomainName() throws Exception {
        assertTrue(PhoenixNetworkMatcher.isValidDomainName("sirius-horoscope.com"));
        assertTrue(PhoenixNetworkMatcher.isValidDomainName("orionhoroscope.com"));
    }

    @Test
    public void isValidIPAddress() throws Exception {
        assertTrue(PhoenixNetworkMatcher.isValidDomainName("192.16.18.1"));
        assertTrue(PhoenixNetworkMatcher.isValidDomainName("192.016.018.001"));
    }

}