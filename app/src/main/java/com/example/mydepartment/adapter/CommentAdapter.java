package com.example.mydepartment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Comment> comments;

    private CommentAdapter.OnItemClickListener listener;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    public void setListener(CommentAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.name.setText(comment.name);
        viewHolder.text.setText(comment.text);
        if (comment.isTeacher) {
            viewHolder.teacher.setVisibility(View.VISIBLE);
        }
        if (comment.avatar != null) {
            viewHolder.avatar.setImageBitmap(comment.avatar);
        }
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, text, teacher;
        final ImageView avatar;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_section);
            text = view.findViewById(R.id.text_section);
            teacher = view.findViewById(R.id.text_view_teacher);
            avatar = view.findViewById(R.id.image_view_avatar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class Comment {
        public String name, text;
        public boolean isTeacher = false;
        public Bitmap avatar = null;

        public Comment(String name, String text) {
            this.name = name;
            this.text = text;
        }
    }
}
