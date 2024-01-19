package com.ducanh.care_your_love.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ducanh.care_your_love.Models.Post;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.PostDetailActivity;
import com.ducanh.care_your_love.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private ArrayList<Post> dataList;
    private Context context;

    public PostAdapter(ArrayList<Post> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
        Post post = dataList.get(position);
        databaseReference.child(post.user_uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    holder.postConsultantName.setText(user.name);
                }
            }
        });

        holder.postTitle.setText(dataList.get(position).title);
        holder.postContent.setText(dataList.get(position).content);
        Glide.with(context).load(dataList.get(position).image).into(holder.postImage);
        holder.postContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postUUID", post.uuid);
                context.startActivity(intent);
            }
        });
        holder.labelCountComment.setText("(" + post.countComments() + ")");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postConsultantName;
        TextView postTitle;
        TextView postContent;
        ImageView postImage;
        CardView postContainer;
        TextView labelCountComment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            postConsultantName = itemView.findViewById(R.id.post_consultant_name);
            postTitle = itemView.findViewById(R.id.post_title);
            postContent = itemView.findViewById(R.id.post_content);
            postImage = itemView.findViewById(R.id.post_image);
            postContainer = itemView.findViewById(R.id.post_container);
            labelCountComment = itemView.findViewById(R.id.label_count_comment);
        }
    }
}
