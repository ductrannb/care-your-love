package com.ducanh.care_your_love;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.commons.Store;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextView linkMain;
    private EditText inputCurrentPassword;
    private EditText inputNewPassword;
    private EditText inputConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        linkMain = findViewById(R.id.link_main);
        inputCurrentPassword = findViewById(R.id.input_current_password);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);

        // tạo underline
        linkMain.setPaintFlags(linkMain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //xử lý textview quay lại main
        linkMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = inputCurrentPassword.getText().toString();
                String newPassword = inputNewPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                //ktra mk hiện tại có khớp với userlogin
                if (currentPassword.equals(Store.userLogin.password)) {
                    if (newPassword.equals(confirmPassword)) {
                        Store.userLogin.password = newPassword;
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
                        databaseReference.child(Store.userLogin.uuid).setValue(Store.userLogin);

                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu xác nhận không đúng!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
