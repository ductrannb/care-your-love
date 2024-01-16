package com.ducanh.care_your_love.Models;

import com.ducanh.care_your_love.commons.Common;

public class Message {
    public String uuid;
    public String from_uuid;
    public String content;
    public String created_at;

    public Message() { }

    public Message(String from_uuid, String content) {
        this.uuid = Common.generateUUID();
        this.from_uuid = from_uuid;
        this.content = content;
        this.created_at = Common.getTimestampNow();
    }
}

