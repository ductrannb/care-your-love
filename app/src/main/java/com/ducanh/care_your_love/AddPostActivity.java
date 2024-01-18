package com.ducanh.care_your_love;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ducanh.care_your_love.Models.Post;
import com.ducanh.care_your_love.commons.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;

public class AddPostActivity extends AppCompatActivity {
    private TextView linkBack;
    private ImageView uploadImage;
    private Uri imageUri;
    private EditText inputTitle;
    private EditText inputContent;
    private Button btnPost;
    private boolean isEdit;
    private Post postEdit;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        linkBack = findViewById(R.id.link_back);
        inputTitle = findViewById(R.id.input_title);
        inputContent = findViewById(R.id.input_content);
        uploadImage = findViewById(R.id.uploadImage);
        btnPost = findViewById(R.id.btn_post);

        //Tạo underline cho textview quay lại
        linkBack.setPaintFlags(linkBack.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Lấy uuid từ intent truyền sang
        Intent intent = getIntent();
        String postUUID = intent.getStringExtra("postUUID");
//        String postUUID = "174d1d42-aaad-4e70-add1-8ab89818d6a2";

        if (postUUID == null || postUUID.isEmpty()) {
            isEdit = false;
        } else {
            isEdit = true;
            getPostEdit(postUUID);
        }

        // xử lý chọn ảnh từ thiết bị và hiển thị lên imageview
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { // kết quả là result ok lấy dl từ intent trả về: là uri của hình ảnh bằng các gọi result.getdata
                            Intent data = result.getData();
                            imageUri = data.getData(); // gán uri cho image uri
                            uploadImage.setImageURI(imageUri);// hiển thị hình ảnh lên imageview
                        } else {
                            Toast.makeText(AddPostActivity.this, "Bạn chưa tải ảnh lên", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //xu ly onClick cho uploadimage
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        //xu ly onClick cho btn_post
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputTitle.getText().toString();
                String content = inputContent.getText().toString();
                if (title.isEmpty() && content.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Vui lòng nhập tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUri != null) {
                        uploadToFirebase(imageUri);
                    } else {
                        Toast.makeText(AddPostActivity.this, "Bạn chưa nhập hình ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getPostEdit(String uuid) {
        databaseReference.child(uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    postEdit = task.getResult().getValue(Post.class);
                    fillData();
                } else {
                    Log.e("GetPostEdit", task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    private void fillData() {
        inputTitle.setText(postEdit.title);
        inputContent.setText(postEdit.content);
    }
    //outside onCreate
    private void uploadToFirebase(Uri uri) {
        String title = inputTitle.getText().toString();
        String content = inputContent.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Post post = new Post(Store.userLogin.uuid, title, content, uri.toString(), null);
                        databaseReference.child(post.uuid).setValue(post); //ghi dữ liệu
                        Toast.makeText(AddPostActivity.this, "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
