package com.ducanh.care_your_love.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.R;

import java.util.List;

public class AdminListUserAdapter extends RecyclerView.Adapter<AdminListUserAdapter.AdminListUserViewHolder> {
    private List<User> listUsers;

    public AdminListUserAdapter(List<User> listUsers) {
        this.listUsers = listUsers;
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
                    //pending
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pending
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
