package com.ducanh.care_your_love;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
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


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data.getData();
//                            imageButton.setImageURI(imageUri);
                            uploadImage.setImageURI(imageUri);
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
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(AddPostActivity.this, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT ).show();
                }
            }
        });
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
                        databaseReference.child(post.uuid).setValue(post);
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
