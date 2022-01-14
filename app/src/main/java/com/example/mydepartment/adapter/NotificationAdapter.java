package com.example.mydepartment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private ArrayList<Notification> notifications;
    private OnItemClickListener listener;


    public NotificationAdapter(Context context, ArrayList<Notification> array) {
        this.inflater = LayoutInflater.from(context);
        notifications = array;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
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
        viewHolder.send.setOnClickListener(v -> listener.onItemClick(notif, position));
        viewHolder.text.setText(notif.text);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final Button send;

        ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text_view_notifications);
            send = view.findViewById(R.id.button_item_send);
        }
    }

    public static class Notification {
        public String text;
        public ArrayList<String> ids;
    }

    public interface OnItemClickListener {
        void onItemClick(Notification n, int position);
    }
}
