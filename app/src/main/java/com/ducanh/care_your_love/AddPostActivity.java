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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    // khai báo Textview, imageview ....
    private TextView linkAddPost;
    private ImageButton uploadImage;
    private ImageView imageView;
    private Uri imageUri;
    private EditText inputTitle;
    private EditText inputContent;
    private Button btnPost;
    private boolean isEdit;
    private Post postEdit;
    // lấy dl từ firebase qua posts trong model Post
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");

    // lấy ảnh từ firebase
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        // ánh xạ từ layout sang

        linkAddPost = findViewById(R.id.link_add_post);
        inputTitle = findViewById(R.id.input_title);
        inputContent = findViewById(R.id.input_content);
        uploadImage = findViewById(R.id.uploadImage);
        imageView = findViewById(R.id.image_view);
        btnPost = findViewById(R.id.btn_post);

        //Tạo underline cho textview quay lại
        linkAddPost.setPaintFlags(linkAddPost.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // xu ly onclick link_add_post, khi click vào chuyển sang màn MainActivity
        linkAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Lấy uuid từ intent (PostDetaliActivity) truyền sang
        String postUUID = getIntent().getStringExtra("postUUID");

        // kiểm tra là màn sửa hay tạo
        if (postUUID == null || postUUID.isEmpty()) {
            isEdit = false;
        } else {
            isEdit = true;
            getPostEdit(postUUID); // là màn sửa thì gọi đến getPostEdit
        }

        // xử lý chọn ảnh từ thiết bị và hiển thị lên imageview
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult( //ActivityResultLauncher đc dùng để chọn và nhận ảnh từ ng dùng
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() { // xử lý kết quả trả về
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { // kết quả là result ok lấy dl từ intent trả về: là uri của hình ảnh bằng cách gọi result.getdata
                            Intent data = result.getData();
                            imageUri = data.getData(); // gán uri cho image uri
                            imageView.setImageURI(imageUri);// hiển thị hình ảnh lên imageview
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
                    if (postEdit != null) {
                        uploadToFirebase(imageUri);
                    } else {
                        // Xu ly them bai viet
                        if (imageUri != null) {
                            uploadToFirebase(imageUri);
                        }
                        else {
                            Toast.makeText(AddPostActivity.this, "Bạn chưa nhập hình ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    //lấy thông tin bài viết qua uuid
    private void getPostEdit(String uuid) {
        // lấy thông tin bài viết từ firebase về qua uuid
        databaseReference.child(uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) { // thành công thì gán kết quả = postEdit
                    postEdit = task.getResult().getValue(Post.class);
                    fillData();// lấy thông tin thành công thù dùng filldata để đổ dữ liệu ra edittext title, content, và imageview từ dùng 170
                } else {
                    Log.e("GetPostEdit", task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    //sau khi lấy thông tin thì dùng fill data đổ dữ liệu vào giao diện tương ứng
    private void fillData() {
        inputTitle.setText(postEdit.title);
        inputContent.setText(postEdit.content);
        Glide.with(this).load(postEdit.image).into(imageView); //glide hiển thị ảnh
        btnPost.setText("Sửa bài đăng"); // btnPost dc settext thành sửa bài đăng khi click btnPost sẽ xử lý tiếp ở dòng 125
    }
    //outside onCreate
    private void uploadToFirebase(@Nullable Uri uri) { // Nullable vi co the sua khong them anh
        String title = inputTitle.getText().toString(); // lay  edittext
        String content = inputContent.getText().toString();

        if (postEdit != null) {
            if (uri != null) { // sửa aảnh mới
                // Xu ly dang anh khi sua
                StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri)); //System.currentTimeMillis() + "." + getFileExtension(uri): đtặ cái tên cho anh theo thời gian
                imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//putFile là firebase
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // xu ly upload image thành công
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // lấy linnk ảnh
                            @Override
                            public void onSuccess(Uri uri) {
                                postEdit.image = uri.toString();
                                // Sau khi tai ảnh mới thành công, update đường dẫn ảnh mới vào postedit
                                postEdit.title = title;
                                postEdit.content = content;
                                databaseReference.child(postEdit.uuid).setValue(postEdit); //ghi dữ liệu cua postedit

                                Toast.makeText(AddPostActivity.this, "Cập nhật bài viết thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            } else {
                // không có ảnh mới được tải lên, chỉ update title, content
                postEdit.title = title; // chỉ xét lại
                postEdit.content = content;
                databaseReference.child(postEdit.uuid).setValue(postEdit); //ghi dữ liệu
                Toast.makeText(AddPostActivity.this, "Cập nhật bài viết thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(intent);
            }

        } else {
            // Xu ly them bai viet moi
            StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
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
    }
    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
