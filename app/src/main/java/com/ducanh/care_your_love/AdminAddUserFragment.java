package com.ducanh.care_your_love;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ducanh.care_your_love.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminAddUserFragment extends Fragment {
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputEmail;
    private EditText inputName;
    private RadioButton radioButtonUserNormal;
    private RadioButton radioButtonUserConsultant;
    private Button btnRegister;
    private Button btnBack;
    private TextView labelTitle;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private User userEdit;

    public AdminAddUserFragment() { }

    public AdminAddUserFragment(User userEdit) {
        this.userEdit = userEdit;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_add_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(User.REFERENCE_NAME);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                int role = radioButtonUserNormal.isChecked() ? User.ROLE_USER_NORMAL : User.ROLE_CONSULTANT;
                if (userEdit == null) {
                    User user = new User(username, password, name, email, role, null, User.GENDER_MALE);
                    updateOrCreateUser(user);
                } else {
                    userEdit.username = username;
                    userEdit.password = password;
                    userEdit.name = name;
                    userEdit.email = email;
                    userEdit.role = role;
                    updateOrCreateUser(userEdit);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AdminActivity.class));
            }
        });
    }

    public void updateOrCreateUser(User user) {
        databaseReference.child(user.uuid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), user.equals(userEdit) ? "Sửa thành công" : "Thêm thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), AdminActivity.class));
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AdminCreateUser", task.getException().getMessage(), task.getException());
                }
            }
        });
    }

    public void initUI(@NonNull View view) {
        inputUsername = view.findViewById(R.id.input_username);
        inputPassword = view.findViewById(R.id.input_password);
        inputEmail = view.findViewById(R.id.input_email);
        inputName = view.findViewById(R.id.input_name);
        radioButtonUserNormal = view.findViewById(R.id.radio_btn_user_normal);
        radioButtonUserConsultant = view.findViewById(R.id.radio_btn_user_consultant);
        btnRegister = view.findViewById(R.id.btn_register);
        btnBack = view.findViewById(R.id.btn_back);
        labelTitle = view.findViewById(R.id.label_title);
        if (userEdit != null) {
            fillData();
            labelTitle.setText("Sửa tài khoản");
            btnRegister.setText("Lưu");
        }
    }

    public void fillData() {
        inputUsername.setText(userEdit.username);
        inputPassword.setText(userEdit.password);
        inputEmail.setText(userEdit.email);
        inputName.setText(userEdit.name);
        if (userEdit.role == User.ROLE_USER_NORMAL) {
            radioButtonUserNormal.setChecked(true);
        } else {
            radioButtonUserConsultant.setChecked(true);
        }
    }
}