package com.ducanh.care_your_love;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.commons.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextView linkRegister;
    private EditText inputUsername;
    private EditText inputPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkRegister = findViewById(R.id.link_register);
        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);

        // tạo underline
        linkRegister.setPaintFlags(linkRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Thêm sự kiện onClick cho "Đăng ky"
        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // them sự kiện onClick cho Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim(); //trim() loại bỏ khoảng trắng 2 đầu
                String password = inputPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Bạn chưa nhập tên đăng nhập và mật khẩu!", Toast.LENGTH_SHORT).show();
                } else {
                    login(username,password);
                }
            }
        });
    }

    private void login(String username, String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Query query = databaseReference.orderByChild("username").equalTo(inputUsername.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //dataSnapshot chứa data . user lấy ra từ dataSnapshot
                boolean isLoginSuccesful = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.password.equals(inputPassword.getText().toString())) {
                        isLoginSuccesful = true;
                        Store.userLogin = user;
                        break;
                    }
                }

                if (isLoginSuccesful) {
                    if (Store.userLogin.role == User.ROLE_USER_NORMAL || Store.userLogin.role == User.ROLE_CONSULTANT) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (Store.userLogin.role == User.ROLE_ADMIN) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    }
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}