package com.ducanh.care_your_love;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.Adapters.PostAdapter;
import com.ducanh.care_your_love.Models.Post;
import com.ducanh.care_your_love.Models.User;
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

public class ProfileActivity extends AppCompatActivity {
    private TextView labelName;
    private Button btnInbox;
    private ImageButton btnBackToHome;
    private RecyclerView recyclerViewListPost;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    private User consultant;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initUI();
        postAdapter = new PostAdapter(posts, this);
        recyclerViewListPost.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewListPost.setAdapter(postAdapter);

        String consultantUUID = getIntent().getStringExtra("consultantUUID");
        if (consultantUUID != null && !consultantUUID.isEmpty()) {
            DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
            referenceUser.child(consultantUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        consultant = task.getResult().getValue(User.class);
                        labelName.setText(consultant.name);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Post.REFERENCE_NAME);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Post post = dataSnapshot.getValue(Post.class);
                                    boolean isExist = false;
                                    for (int i = 0; i < posts.size(); i++) {
                                        if (posts.get(i).uuid.equals(post.uuid)) {
                                            posts.set(i, post);
                                            isExist = true;
                                            break;
                                        }
                                    }
                                    if (!isExist && post.user_uuid.equals(consultant.uuid)) {
                                        posts.add(post);
                                    }
                                }
                                postAdapter.setDataList(posts);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
        btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putExtra("partnerUUID", consultant.uuid);
                startActivity(intent);
            }
        });

        labelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, InfoConsultantActivity.class);
                intent.putExtra("uuid", consultant.uuid);
                startActivity(intent);
            }
        });
    }

    public void initUI() {
        labelName = findViewById(R.id.label_name);
        btnInbox = findViewById(R.id.btn_inbox);
        recyclerViewListPost = findViewById(R.id.rcv_list_post);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
        if (Store.userLogin.role != User.ROLE_USER_NORMAL) {
            btnInbox.setVisibility(View.INVISIBLE);
        }
    }
}
