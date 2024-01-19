package com.ducanh.care_your_love.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.ChatActivity;
import com.ducanh.care_your_love.Models.Message;
import com.ducanh.care_your_love.R;
import com.ducanh.care_your_love.commons.Store;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Message> messages;
    private ChatActivity chatActivity;

    public MessageAdapter(List<Message> messages, ChatActivity chatActivity) {
        this.messages = messages;
        this.chatActivity = chatActivity;
        notifyDataSetChanged();
    }

    public void newMessage(Message message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message != null) {
            int paddingMsg = 200;
            holder.message.setText(message.content);
            if (message.from_uuid.equals(Store.userLogin.uuid)) {
                holder.container.setPadding(paddingMsg, 0, 5, 0);
                holder.message.setBackgroundResource(R.color.input_border);
                holder.message.setTextColor(ContextCompat.getColor(chatActivity.getContext(), R.color.white));
            } else {
                holder.container.setPadding(5, 0, paddingMsg, 0);
                holder.message.setBackgroundResource(R.color.light_gray);
                holder.message.setTextColor(ContextCompat.getColor(chatActivity.getContext(), R.color.black));
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private LinearLayout container;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_item);
            container = itemView.findViewById(R.id.chat_message_container);
        }
    }
}
