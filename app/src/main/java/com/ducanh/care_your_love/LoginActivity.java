package com.ducanh.care_your_love;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private TextView linkRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // tạo underline
        linkRegister = findViewById(R.id.link_register);
        linkRegister.setPaintFlags(linkRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Thêm sự kiện onClick cho "Đăng ky"
        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}