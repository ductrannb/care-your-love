package com.example.careyourlove;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText emailedit, passedit;
    private Button btnlogin, btnregister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailedit = findViewById(R.id.email);
        passedit = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnlogin);
        btnregister = findViewById(R.id.btnregister);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }

        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }

        });
    }

    private void register() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

//    private void startActivities(Intent i) {
//    }

    private void login() {
        String email, pass;
        email = emailedit.getText().toString();
        pass = passedit.getText().toString();
        // kieerm tra xem da nhap email password chua
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,"Vui long nhap email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this,"Vui long nhap password!", Toast.LENGTH_SHORT).show();
            return;
        }
        // dang nhap email, pass thanh cong goi ham onComplate va tra qua bien task
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Dang nhap thanh cong", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Dang nhap khong thanh cong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




