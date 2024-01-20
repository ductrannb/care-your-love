package com.ducanh.care_your_love;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ducanh.care_your_love.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoConsultantActivity extends AppCompatActivity {
    private User consultant;
    private TextView labelName;
    private TextView labelPhone;
    private TextView labelGmail;
    private TextView labelAddress;
    private TextView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_consultant);

        labelName = findViewById(R.id.label_name);
        labelPhone = findViewById(R.id.label_phone);
        labelGmail = findViewById(R.id.label_gmail);
        labelAddress = findViewById(R.id.label_address);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoConsultantActivity.this, MainActivity.class));
            }
        });

        String consultantUUID = getIntent().getStringExtra("uuid");
        if (consultantUUID != null && !consultantUUID.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
            databaseReference.child(consultantUUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        consultant = task.getResult().getValue(User.class);
                        labelName.setText(consultant.name);
                        labelPhone.setText(consultant.phone);
                        labelGmail.setText(consultant.email);
                        labelAddress.setText(consultant.address);
                    }
                }
            });
        }
    }
}
