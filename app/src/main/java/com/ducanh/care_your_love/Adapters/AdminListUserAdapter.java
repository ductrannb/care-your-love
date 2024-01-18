package com.ducanh.care_your_love.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.AdminAddUserFragment;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminListUserAdapter extends RecyclerView.Adapter<AdminListUserAdapter.AdminListUserViewHolder> {
    private List<User> listUsers;
    private Context context;
    public AdminListUserAdapter(List<User> listUsers, Context context) {
        this.listUsers = listUsers;
        context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminListUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminListUserViewHolder(view);
    }

    public void updateListUsers(List<User> listUsers) {
        this.listUsers = listUsers;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AdminListUserViewHolder holder, int position) {
        User user = listUsers.get(position);
        if (user != null) {
            holder.labelName.setText(user.name);
            holder.labelRole.setText(user.getRoleName());
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminAddUserFragment(user)).commit();
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
                    databaseReference.child(user.uuid)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        listUsers.remove(user);
                                        notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    } else  {
                                        Log.e("DeleteUser", task.getException().getMessage(), task.getException());
                                    }
                                }
                            });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listUsers != null ? listUsers.size() : 0;
    }

    public class AdminListUserViewHolder extends RecyclerView.ViewHolder {
        private TextView labelName;
        private TextView labelRole;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public AdminListUserViewHolder(@NonNull View itemView) {
            super(itemView);
            labelName = itemView.findViewById(R.id.label_name);
            labelRole = itemView.findViewById(R.id.label_role);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
