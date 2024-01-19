package com.ducanh.care_your_love;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducanh.care_your_love.Adapters.AdminListUserAdapter;
import com.ducanh.care_your_love.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {
    private List<User> listUsers = new ArrayList<>();
    private RecyclerView recyclerViewListUser;
    private AdminListUserAdapter adminListUserAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(User.REFERENCE_NAME);
        reference.orderByChild("created_at").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user.role == User.ROLE_ADMIN) {
                        continue;
                    }
                    boolean isExist = false;
                    for (int i=0; i < listUsers.size(); i++) {
                        if (listUsers.get(i).uuid.equals(user.uuid)) {
                            updateUser(i, user);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        addUser(data.getValue(User.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoadListUSer", "onCancelled: ", error.toException());
            }
        });

        adminListUserAdapter = new AdminListUserAdapter(listUsers, getContext());

        recyclerViewListUser = view.findViewById(R.id.rcv_admin_list_user);
        recyclerViewListUser.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewListUser.setAdapter(adminListUserAdapter);

    }

    public void addUser(User user) {
        listUsers.add(user);
        adminListUserAdapter.updateListUsers(listUsers);
    }

    public void updateUser(int index, User user) {
        listUsers.set(index, user);
        adminListUserAdapter.updateListUsers(listUsers);
    }
}