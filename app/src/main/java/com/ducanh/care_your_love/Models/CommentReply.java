package com.ducanh.care_your_love.Models;

import androidx.annotation.Nullable;

import com.ducanh.care_your_love.commons.Common;

import java.util.ArrayList;
import java.util.List;

public class CommentReply {
    public String uuid;
    public String user_uuid;
    public String content;
    public String created_at;

    public CommentReply() { }

    public CommentReply(String user_uuid, String content) {
        this.uuid = Common.generateUUID();
        this.user_uuid = user_uuid;
        this.content = content;
        this.created_at = Common.getTimestampNow();
    }
}
