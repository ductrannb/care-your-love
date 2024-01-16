package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ducanh.care_your_love.Adapters.MessageAdapter;
import com.ducanh.care_your_love.Models.Chat;
import com.ducanh.care_your_love.Models.Message;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView labelPartnerName;
    private RecyclerView recyclerViewMessages;
    private EditText inputMessage;
    private ImageButton btnSendMessage;
    private MessageAdapter messageAdapter;
    private Chat chat;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private static final String CHAT_LOG_TAG = "Chat";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUi();
        // init firebase database instance
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(Chat.REFERENCE_NAME);
        getChat();

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(Store.userLoggedInUUID, inputMessage.getText().toString());
                sendChat(message);
            }
        });
    }

    public void sendChat(Message message) {
        chat.messages.add(message);
        reference.child(chat.uuid).setValue(chat);
        inputMessage.setText("");
    }

    public void getChat() {
        String chatUUID = "2da9a4fa-5119-4a33-94aa-30cfb6de1f6f";
        reference.child(chatUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    chat = task.getResult().getValue(Chat.class);
                    labelPartnerName.setText(getChatPartnerName());
                    messageAdapter = new MessageAdapter(chat.messages, ChatActivity.this);
                    recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewMessages.setAdapter(messageAdapter);
                    setEventListenerChat();
                } else {
                    Log.w(CHAT_LOG_TAG, "Get chat", task.getException());
                }
            }
        });
    }

    public void setEventListenerChat() {
        reference.child(chat.uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat = snapshot.getValue(Chat.class);
                if (chat.messages.size() > messageAdapter.getItemCount()) {
                    messageAdapter.newMessage(chat.messages.get(chat.messages.size() - 1));
                }
                recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(CHAT_LOG_TAG, "Load chat: onCancelled", error.toException());
            }
        });
    }

    public String getChatPartnerName() {
        if (chat == null) {
            return "...";
        }
        if (chat.user_uuid.equals(Store.userLoggedInUUID)) {
            return "Nguoi dung"; // pending get info user
        }
        return "Chuyen gia"; //pending get info consultant
    }

    public Context getContext() {
        return ChatActivity.this.getApplicationContext();
    }


    public void initUi() {
        btnBack = findViewById(R.id.chat_btn_back);
        labelPartnerName = findViewById(R.id.chat_partner_name);
        recyclerViewMessages = findViewById(R.id.rcv_list_message);
        inputMessage = findViewById(R.id.chat_input_message);
        btnSendMessage = findViewById(R.id.chat_btn_send_message);
    }
}