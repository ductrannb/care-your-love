package com.ducanh.care_your_love.Models;

import androidx.annotation.Nullable;

import com.ducanh.care_your_love.commons.Common;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    public String uuid;
    public String user_uuid;
    public String content;
    public List<Comment> replies;
    public String created_at;

    public Comment(String user_uuid, String content, @Nullable List<Comment> replies) {
        this.uuid = Common.generateUUID();
        this.user_uuid = user_uuid;
        this.content = content;
        this.replies = replies != null ? replies : new ArrayList<>();
        this.created_at = Common.getTimestampNow();
    }
}
