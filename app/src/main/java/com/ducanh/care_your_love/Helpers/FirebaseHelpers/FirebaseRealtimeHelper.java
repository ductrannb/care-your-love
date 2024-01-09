package com.ducanh.care_your_love.Helpers.FirebaseHelpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class FirebaseRealtimeHelper {
    public static final String HELLO_WORLD_REF = "Hello World";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private Class<?> classData;

    public FirebaseRealtimeHelper(String reference, Object classData) {
        this.classData = classData.getClass();
        databaseReference = database.getReference(reference);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //pending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //pending
            }
        });
    }

    public void setValue(Object value) {
        databaseReference.setValue(value);
    }
}
