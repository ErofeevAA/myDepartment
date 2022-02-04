package com.example.mydepartment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private ArrayList<Notification> notifications;
    private OnItemButtonClickListener itemButtonClickListener;
    private OnItemClickListener itemClickListener;

    public NotificationAdapter(Context context, ArrayList<Notification> array) {
        this.inflater = LayoutInflater.from(context);
        notifications = array;
    }

    public void setButtonClickListener(OnItemButtonClickListener listener) {
        this.itemButtonClickListener = listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void remove(int pos) {
        notifications.remove(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_notification, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notif = notifications.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.root.setOnClickListener(v -> itemClickListener.onItemClick(notif, position));
        viewHolder.send.setOnClickListener(v -> itemButtonClickListener.onItemClick(notif, position));
        viewHolder.text.setText(notif.text);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final Button send;
        final LinearLayout root;

        ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text_view_notifications);
            send = view.findViewById(R.id.button_item_send);
            root = view.findViewById(R.id.layout_item_notification);
        }
    }

    public static class Notification {
        public String text;
        public ArrayList<String> ids;
        public String subjectID = "";
        public String sectionID = "";
    }

    public interface OnItemButtonClickListener {
        void onItemClick(Notification n, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(Notification n, int position);
    }
}
