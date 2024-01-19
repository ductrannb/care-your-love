package com.ducanh.care_your_love.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ducanh.care_your_love.Models.Comment;
import com.ducanh.care_your_love.Models.CommentReply;
import com.ducanh.care_your_love.Models.User;
import com.ducanh.care_your_love.R;
import com.ducanh.care_your_love.commons.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.CommentReplyViewHolder>{
    private List<CommentReply> commentReplies;
    private Context context;

    public CommentReplyAdapter(Context context, @Nullable List<CommentReply> commentReplies) {
        this.context = context;
        this.commentReplies = commentReplies;
    }

    public void setComments(@Nullable List<CommentReply> commentReplies) {
        this.commentReplies = commentReplies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,false);
        return new CommentReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentReplyViewHolder holder, int position) {
        CommentReply commentReply = commentReplies.get(position);
        if (commentReply != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
            reference.child(commentReply.user_uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        holder.commentName.setText(task.getResult().getValue(User.class).name);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.commentContent.setText(commentReply.content);
            holder.commentReplyList.setVisibility(View.INVISIBLE);
            holder.btnReplyComment.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return commentReplies != null ? commentReplies.size() : 0;
    }

    public class CommentReplyViewHolder extends RecyclerView.ViewHolder{
        private TextView commentName;
        private TextView commentContent;
        private RecyclerView commentReplyList;
        private TextView btnReplyComment;

        public CommentReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            commentName = itemView.findViewById(R.id.comment_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentReplyList = itemView.findViewById(R.id.comment_reply_list);
            btnReplyComment = itemView.findViewById(R.id.btn_reply_comment);
        }
    }
}
