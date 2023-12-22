package com.example.careyourlove;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.careyourlove.Database.AppDatabase;
import com.example.careyourlove.Models.User;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button btnAdd = view.findViewById(R.id.btn_test_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User( "duc@gmail.com", "123456", "Trần Xuân Đức");
                AppDatabase.getInstance(requireContext()).userDao().insert(user);
                Toast.makeText(requireContext(),"Sucess", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}