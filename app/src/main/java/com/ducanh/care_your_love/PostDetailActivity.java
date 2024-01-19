package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ducanh.care_your_love.Adapters.CommentAdapter;
import com.ducanh.care_your_love.Models.Post;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostDetailActivity extends AppCompatActivity {
    private Post post;
    private ImageButton btnBackToHome;
    private ImageButton btnEditPost;
    private ImageButton btnDeletePost;
    private TextView postDetailConsultantName;
    private TextView postDetailContent;
    private ImageView postDetailImage;
    private RecyclerView postDetailListComment;
    private EditText inputContentComment;
    private ImageButton btnSendComment;
    private DatabaseReference databaseReference;
    private CommentAdapter commentAdapter;
    private TextView labelNoComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initUI();
        databaseReference = FirebaseDatabase.getInstance().getReference(Post.REFERENCE_NAME);
        getData();
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            }
        });

        btnEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xu ly dan toi trang edit
                Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                intent.putExtra("postUUID", post.uuid);
                startActivity(intent);
            }
        });

        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xu ly xoa bai viet
                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa bài viết không?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child(post.uuid).removeValue(); // xoa bai viet khoi db
                        Toast.makeText(PostDetailActivity.this, "Bài viết đã được xóa", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PostDetailActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void getData() {
        String postUUID = getIntent().getStringExtra("postUUID");
        if (postUUID != null && !postUUID.isEmpty()) {
            databaseReference.child(postUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        post = task.getResult().getValue(Post.class);
                        fillData();
                    } else {
                        Toast.makeText(PostDetailActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void fillData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
        reference.child(post.user_uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    postDetailConsultantName.setText(task.getResult().getValue(User.class).name);
                    postDetailContent.setText(post.content);
                    Glide.with(PostDetailActivity.this).load(post.image).into(postDetailImage);
                    commentAdapter = new CommentAdapter(PostDetailActivity.this, post.comments);
                    postDetailListComment.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
                    postDetailListComment.setAdapter(commentAdapter);
                    if (post.user_uuid.equals(Store.userLogin.uuid)) {
                        btnEditPost.setVisibility(View.VISIBLE);
                        btnDeletePost.setVisibility(View.VISIBLE);
                    }
                    if (post.comments == null || post.comments.size() == 0) {
                        labelNoComment.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void initUI() {
        btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnEditPost = findViewById(R.id.btn_edit_post);
        btnDeletePost = findViewById(R.id.btn_delete_post);
        postDetailConsultantName = findViewById(R.id.post_detail_consultant_name);
        postDetailContent = findViewById(R.id.post_detail_content);
        postDetailImage = findViewById(R.id.post_detail_image);
        postDetailListComment = findViewById(R.id.post_detail_comment_list);
        inputContentComment = findViewById(R.id.input_content_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);
        labelNoComment = findViewById(R.id.label_no_comment);
    }
}