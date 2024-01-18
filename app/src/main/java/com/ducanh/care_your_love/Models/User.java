package com.ducanh.care_your_love.Models;

import androidx.annotation.Nullable;

import com.ducanh.care_your_love.commons.Common;

public class User {
    public String uuid;
    public String username;
    public String password;
    public String name;
    public String email;
    public int gender;
    public String birthday;
    public int role;
    public String created_at;

    public static final String REFERENCE_NAME = "users"; //ten bang

    public static final int GENDER_MALE  = 1;
    public static final int GENDER_FEMALE = 2;
    public static final int ROLE_USER_NORMAL = 1;
    public static final int ROLE_CONSULTANT = 2;
    public static final int ROLE_ADMIN = 3;

    public User() { }

    public User(String username, String password, String name, String email, int role, @Nullable String birthday, @Nullable int gender) {
        this.uuid = Common.generateUUID(); //tao uuid random
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.role = role;
        this.created_at = Common.getTimestampNow(); //lay thoi gian hien tai
    }

    public String getRoleName() {
        switch (this.role) {
            case ROLE_USER_NORMAL:
                return "Người dùng";
            case ROLE_CONSULTANT:
                return "Chuyên gia";
            case ROLE_ADMIN:
                return "Admin";
            default:
                return "NONE";
        }
    }
}
