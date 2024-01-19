package com.ducanh.care_your_love.Models;

import androidx.annotation.Nullable;

import com.ducanh.care_your_love.commons.Common;

import java.util.ArrayList;
import java.util.List;

public class Post {
    public String uuid;
    public String user_uuid;
    public String title;
    public String content;
    public String image;
    public String created_at;
    public List<Comment> comments;
    public static final String REFERENCE_NAME  = "posts";

    public Post() {}

    public Post(String user_uuid, String title, String content, String image, @Nullable List<Comment> comments) {
        this.uuid = Common.generateUUID();
        this.user_uuid = user_uuid;
        this.title = title;
        this.content = content;
        this.image = image;
        this.comments = comments != null ? comments : new ArrayList<>();
        this.created_at = Common.getTimestampNow();
    }
}
