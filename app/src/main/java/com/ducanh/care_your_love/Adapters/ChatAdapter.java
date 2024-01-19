package com.ducanh.care_your_love.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.Models.Chat;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.R;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHoder> {
    private List<Chat> chats;
    private Activity activity;

    public ChatAdapter(Activity activity, List<Chat> chats) {
        this.activity = activity;
        this.chats = chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ChatViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHoder holder, int position) {
        Chat chat = chats.get(position);
        if (chat != null) {
            String chatUserUUID = Store.userLogin.uuid.equals(chat.user_uuid) ? chat.user_uuid : chat.consultant_uuid;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
            databaseReference.child(chatUserUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        holder.chatName.setText(task.getResult().getValue(User.class).name);
                    }
                }
            });
            holder.chatLatestMessage.setText(chat.messages.get(chat.messages.size()-1).content);
        }
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

    public class ChatViewHoder extends RecyclerView.ViewHolder {
        private TextView chatName;
        private TextView chatLatestMessage;
        public ChatViewHoder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chat_name);
            chatLatestMessage = itemView.findViewById(R.id.chat_latest_message);
        }
    }
}
