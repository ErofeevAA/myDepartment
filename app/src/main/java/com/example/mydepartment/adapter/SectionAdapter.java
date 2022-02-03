package com.example.mydepartment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;

import java.util.ArrayList;

public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Section> sections;

    private OnItemClickListener listener;
    private OnPDFClickListener pdfClickListener;

    public SectionAdapter(Context context, ArrayList<Section> array) {
        this.inflater = LayoutInflater.from(context);
        this.sections = array;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPDFClickListener(OnPDFClickListener listener) {
        this.pdfClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Section section = sections.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.name.setText(section.name);
        viewHolder.text.setText(section.text);
        if (section.fileLink != null) {
            viewHolder.pdf.setVisibility(View.VISIBLE);
            viewHolder.pdf.setOnClickListener(v -> pdfClickListener.onClick(section.fileLink));
        }

        viewHolder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, text;
        final ImageView pdf;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_section);
            text = view.findViewById(R.id.text_section);
            pdf = view.findViewById(R.id.item_icon_pdf);
        }
    }

    public static class Section {
        public String name, text;
        public String fileLink = null;

        public Section(String name, String text) {
            this.name = name;
            this.text = text;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnPDFClickListener {
        void onClick(String fileLink);
    }
}