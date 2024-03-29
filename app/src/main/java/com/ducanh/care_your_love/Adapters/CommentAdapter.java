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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<Comment> comments;
    private List<CommentReply> commentReplies;
    private Context context;
    public static boolean isReply = false;
    public static Comment commentReplying;

    public CommentAdapter(Context context, @Nullable List<Comment> comments) {
        this.comments = comments;
        this.context = context;
        this.commentReplies = commentReplies;
    }

    public void setComments(@Nullable List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void unReplyComment() {
        isReply = false;
        commentReplying = null;
    }

    public boolean isIsReply() {
        return isReply;
    }

    public Comment getCommentReplying() {
        return commentReplying;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        if (comment != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME);
            reference.child(comment.user_uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        holder.commentName.setText(task.getResult().getValue(User.class).name);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.commentContent.setText(comment.content);

            CommentReplyAdapter commentReplyAdapter = new CommentReplyAdapter(context, comment.replies);
            holder.commentReplyList.setLayoutManager(new LinearLayoutManager(context));
            holder.commentReplyList.setAdapter(commentReplyAdapter);

            holder.btnReplyComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.commentContent.requestFocus();
                    Common.openKeyboard((Activity)context);
                    commentReplying = comment;
                    isReply = true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private TextView commentName;
        private TextView commentContent;
        private RecyclerView commentReplyList;
        private TextView btnReplyComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentName = itemView.findViewById(R.id.comment_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentReplyList = itemView.findViewById(R.id.comment_reply_list);
            btnReplyComment = itemView.findViewById(R.id.btn_reply_comment);
        }
    }
}
