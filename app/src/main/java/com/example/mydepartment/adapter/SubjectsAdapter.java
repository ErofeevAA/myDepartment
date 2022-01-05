package com.example.mydepartment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class SubjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Subject> subjects;
    private OnItemClickListener listener;

    public SubjectsAdapter(Context context, ArrayList<Subject> array) {
        this.inflater = LayoutInflater.from(context);
        this.subjects = array;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.name.setText(subject.name);
        viewHolder.teacher.setText(subject.teacher);
        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, teacher;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_subject);
            teacher = view.findViewById(R.id.name_teacher);
        }
    }

    public static class Subject {
        public String name, teacher;

        public Subject(String name, String teacher) {
            this.name = name;
            this.teacher = teacher;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}