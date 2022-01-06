package com.example.mydepartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.mydepartment.adapter.SectionAdapter;
import com.example.mydepartment.databinding.ActivitySectionsBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SectionsActivity extends AppCompatActivity {
    private ActivitySectionsBinding binding;
    private RecyclerView recyclerView;
    private String subjectID;
    private String response = "[]";
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySectionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
        subjectID = getIntent().getStringExtra("subject_id");
        recyclerView = binding.sectionsList;

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        new Thread(runnable).start();
    }

    private final SectionAdapter.OnItemClickListener listener = (int position) -> {
        String s = "error";
        try {
            JSONObject object = jsonArray.getJSONObject(position);
            s = object.getString("name");
            Intent intent = new Intent(SectionsActivity.this, CommentsActivity.class);
            intent.putExtra("subject_id", subjectID);
            intent.putExtra("section_id", object.getString("id"));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("selected", s);
    };

    private final  SectionAdapter.OnPDFClickListener pdfClickListener = (int position) -> {
        Log.d("pdfClick", String.valueOf(position));
        Log.d("pdfClick", "it nothing");
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                new FailDialogBuilder(SectionsActivity.this,
                        getString(R.string.alert_connection_failed)).show();
                return;
            }
            if (response.equals("[]")) {
                binding.textNothing.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            if (msg.what == 200) {
                fillSection();
            }
        }
    };

    private final Runnable runnable = () -> {
        Requests requests = new Requests();
        requests.sections(subjectID);
        response = requests.getResponse();
        Log.d("response", response);
        handler.sendEmptyMessage(requests.getStatusCode());
    };

    private void fillSection() {
        try {
            jsonArray = new JSONArray(response);
            ArrayList<SectionAdapter.Section> sections  = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("name");
                String text = object.getString("text");

                SectionAdapter.Section section = new SectionAdapter.Section(name, text);

                if (!object.getString("file").equals("null")) {
                    section.hasFile = true;
                }

                sections.add(section);
            }
            SectionAdapter adapter = new SectionAdapter(this, sections);
            adapter.setListener(listener);
            adapter.setPDFClickListener(pdfClickListener);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}