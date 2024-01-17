package com.ducanh.care_your_love;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ducanh.care_your_love.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputBirthday;
    private Calendar calendar;
    private TextView linkLogin;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputEmail;
    private EditText inputName;
    private RadioButton radioBtnMale;
    private RadioButton radioBtnFemale;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        linkLogin = findViewById(R.id.link_login);
        inputBirthday = findViewById(R.id.input_birthday);
        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
        inputEmail = findViewById(R.id.input_email);
        inputName = findViewById(R.id.input_name);
        btnRegister = findViewById(R.id.btn_register);
        radioBtnMale = findViewById(R.id.radio_btn_male);
        radioBtnFemale = findViewById(R.id.radio_btn_female);

        // tạo underline
        linkLogin.setPaintFlags(linkLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Thêm sự kiện onClick cho "Đăng nhập"
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // xu ly calendar- input_birthday
        calendar = Calendar.getInstance();
        inputBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // xu ly su kien inclick cho btnregister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                String email = inputEmail.getText().toString();
                String name = inputName.getText().toString();
                String birthday = inputBirthday.getText().toString();
                int gender;
                if (radioBtnMale.isChecked()) {
                    gender = User.GENDER_MALE;
                } else {
                    gender = User.GENDER_FEMALE;
                }

                //truy cap và ghi dư liệu vào fb db
                User user = new User(username, password, name, email, User.ROLE_USER_NORMAL, birthday, gender);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // lấy dtuong firebasedb thông qua getInsteance
                DatabaseReference databaseReference = firebaseDatabase.getReference(User.REFERENCE_NAME);// dung ten bang ben model User: truy cap vao bang reference name trong model user
                databaseReference.child(user.uuid).setValue(user); //

                Toast.makeText(RegisterActivity.this,"Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthdayEditText();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateBirthdayEditText() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        inputBirthday.setText(sdf.format(calendar.getTime()));
    }
}