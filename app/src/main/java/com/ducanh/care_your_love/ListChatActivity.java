package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.ducanh.care_your_love.Adapters.ChatAdapter;
import com.ducanh.care_your_love.Models.Chat;
import com.ducanh.care_your_love.commons.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListChatActivity extends AppCompatActivity {
    private ImageButton btnBackToHome;
    private RecyclerView recyclerViewListChat;
    private ChatAdapter chatAdapter;
    private DatabaseReference databaseReference;
    private List<Chat> chats = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        initUI();
        chatAdapter = new ChatAdapter(this, null);
        databaseReference = FirebaseDatabase.getInstance().getReference(Chat.REFERENCE_NAME);
        getListChat();
        recyclerViewListChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewListChat.setAdapter(chatAdapter);
    }

    private void getListChat() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (Store.userLogin.uuid.equals(chat.user_uuid) || Store.userLogin.uuid.equals(chat.consultant_uuid)) {
                        boolean isExist = false;
                        for (int i=0; i<chats.size(); i++) {
                            if (chats.get(i).uuid.equals(chat.uuid)) {
                                chats.set(i, chat);
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            chats.add(chat);
                        }
                    }
                }
                chatAdapter.setChats(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUI() {
        btnBackToHome = findViewById(R.id.btn_back);
        recyclerViewListChat = findViewById(R.id.list_chat);
    }
}