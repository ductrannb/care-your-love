package com.ducanh.care_your_love.commons;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Common {
    public static String getTimestampNow() {
        return new Timestamp(new Date().getTime()).toString();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static class Store {

    }
}
