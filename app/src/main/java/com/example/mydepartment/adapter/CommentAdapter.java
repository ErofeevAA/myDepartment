package com.example.mydepartment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Comment> comments;

    private CommentAdapter.OnItemClickListener listener;
    private OnPDFClickListener pdfClickListener;


    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    public void setListener(CommentAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPDFClickListener(OnPDFClickListener listener) {
        pdfClickListener = listener;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.name.setText(comment.name);
        viewHolder.text.setText(comment.text);
        if (comment.replyName != null) {
            viewHolder.layoutReplyComment.setVisibility(View.VISIBLE);
            viewHolder.replyToUser.setText(comment.replyName);
        }
        if (comment.avatar != null) {
            Log.d("AdapterAvatar", "We are here");
            viewHolder.avatar.setImageBitmap(comment.avatar);
        }
        if (comment.linkToPdf != null) {
            viewHolder.pdf.setVisibility(View.VISIBLE);
            viewHolder.pdf.setOnClickListener(v -> {
                Log.d("click", comment.linkToPdf);
                pdfClickListener.onItemClick(comment.linkToPdf);
            });
        }
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(comment.id, comment.name));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, text, prevReply, replyToUser;
        final ImageView avatar, pdf;
        final LinearLayout layoutReplyComment;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_view_user_name);
            text = view.findViewById(R.id.item_comment_text);
            prevReply = view.findViewById(R.id.text_view_prev_reply);
            replyToUser = view.findViewById(R.id.text_view_user_get_reply);
            avatar = view.findViewById(R.id.image_view_avatar);
            layoutReplyComment = view.findViewById(R.id.layout_reply_comment);
            pdf = view.findViewById(R.id.image_view_pdf_comment);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String name);
    }

    public interface OnPDFClickListener {
        void onItemClick(String link);
    }

    public static class Comment {
        public String id;
        public String name, text;
        public String replyName = null;
        public String replyID = null;
        public String linkToPdf = null;

        public Bitmap avatar = null;

        public Comment(String name, String text) {
            this.name = name;
            this.text = text;
        }
    }
}
