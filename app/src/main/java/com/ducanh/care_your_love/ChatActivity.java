package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ducanh.care_your_love.Adapters.MessageAdapter;
import com.ducanh.care_your_love.Models.Chat;
import com.ducanh.care_your_love.Models.Message;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private User userPartner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUi();
        // init firebase database instance
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(Chat.REFERENCE_NAME);
        getChat(getIntent().getStringExtra("partnerUUID"));

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(Store.userLoggedInUUID, inputMessage.getText().toString());
                sendChat(message);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, ListChatActivity.class));
            }
        });
    }

    public void sendChat(Message message) {
        if (chat.messages == null) {
            chat.messages = new ArrayList<>();
        }
        chat.messages.add(message);
        reference.child(chat.uuid).setValue(chat);
        inputMessage.setText("");
    }

    public void getChat(String partnerUUID) {
        String user_uuid = Store.userLogin.role == User.ROLE_CONSULTANT ? partnerUUID : Store.userLogin.uuid;
        String consultant_uuid = Store.userLogin.role == User.ROLE_CONSULTANT ? Store.userLogin.uuid : partnerUUID;
        Query query = reference.orderByChild("user_uuid").equalTo(user_uuid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Chat chatObj = dataSnapshot.getValue(Chat.class);
                    if (chatObj.consultant_uuid.equals(consultant_uuid)) {
                        chat = chatObj;
                        break;
                    }
                }
                if (chat == null) {
                    String user_uuid = Store.userLogin.role == User.ROLE_USER_NORMAL ? Store.userLogin.uuid : partnerUUID;
                    String consultant_uuid = Store.userLogin.role == User.ROLE_CONSULTANT ? Store.userLogin.uuid : partnerUUID;
                    chat = new Chat(user_uuid, consultant_uuid, new ArrayList<>());
                    reference.child(chat.uuid).setValue(chat);
                }
                getChatPartnerName();
                messageAdapter = new MessageAdapter(chat.messages, ChatActivity.this);
                recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewMessages.setAdapter(messageAdapter);
                setEventListenerChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setEventListenerChat() {
        reference.child(chat.uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat = snapshot.getValue(Chat.class);
                if (chat.messages != null && chat.messages.size() > messageAdapter.getItemCount()) {
                    messageAdapter.newMessage(chat.messages.get(chat.messages.size() - 1));
                }
                if (messageAdapter.getItemCount() > 0) {
                    recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(CHAT_LOG_TAG, "Load chat: onCancelled", error.toException());
            }
        });
    }

    public void getChatPartnerName() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
        String partnerUUID;
        if (chat.user_uuid.equals(Store.userLogin.uuid)) {
            partnerUUID = chat.consultant_uuid;
        } else {
            partnerUUID = chat.user_uuid;
        }
        databaseReference.child(partnerUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    userPartner = task.getResult().getValue(User.class);
                    labelPartnerName.setText(userPartner.name);
                }
            }
        });
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