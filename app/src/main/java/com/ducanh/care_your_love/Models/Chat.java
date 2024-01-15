package com.ducanh.care_your_love.Models;

import androidx.annotation.Nullable;

import com.ducanh.care_your_love.commons.Common;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public String uuid;
    public String user_uuid;
    public String consultant_uuid;
    public List<Message> messages;
    public String created_at;

    public static final String REFERENCE_NAME = "chats";

    public Chat() { }


    public Chat(String user_uuid, String consultant_uuid, @Nullable List<Message> messages) {
        this.uuid = Common.generateUUID();
        this.user_uuid = user_uuid;
        this.consultant_uuid = consultant_uuid;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.created_at = Common.getTimestampNow();
    }
}
