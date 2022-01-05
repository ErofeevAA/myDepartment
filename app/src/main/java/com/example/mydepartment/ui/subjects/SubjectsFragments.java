package com.example.mydepartment.ui.subjects;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydepartment.R;
import com.example.mydepartment.SectionsActivity;
import com.example.mydepartment.databinding.FragmentSubjectsBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;
import com.example.mydepartment.adapter.SubjectsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectsFragments extends Fragment {

    private FragmentSubjectsBinding binding;

    private LocalStorage storage;

    private RecyclerView subjectsRecyclerView;
    private TextView nothingTextView;

    private JSONArray jsonArray;

    private String response = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        storage = new LocalStorage(requireContext());

        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectsRecyclerView = binding.subjectsList;
        nothingTextView = binding.textNothing;

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        subjectsRecyclerView.setLayoutManager(llm);

        new Thread(loadSubjectsRunnable).start();

        return root;
    }

    private final SubjectsAdapter.OnItemClickListener listener = (int position) -> {
        String s = "error";
        try {
            JSONObject object = jsonArray.getJSONObject(position);
            s = object.getString("name");
            Intent intent = new Intent(requireActivity(), SectionsActivity.class);
            intent.putExtra("subject_id", object.getString("id"));
            requireActivity().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("selected", s);
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("handler", response);
            if (msg.what == 0) {
                new FailDialogBuilder(requireActivity(),
                        getString(R.string.alert_connection_failed)).show();
                return;
            }
            if (msg.what == 404 || response.equals("[]")) {
                nothingTextView.setVisibility(View.VISIBLE);
                subjectsRecyclerView.setVisibility(View.GONE);
                return;
            }
            fillSubjects();
        }
    };

    private final Runnable loadSubjectsRunnable = () -> {
        Requests requests = new Requests();
        requests.setToken(storage.getToken());
        requests.subjects();
        response = requests.getResponse();
        Log.d("statusCode", String.valueOf(requests.getStatusCode()));
        handler.sendEmptyMessage(requests.getStatusCode());
    };

    private void fillSubjects() {
        if (response == null) {
            Log.d("fillObject", "null response");
            return;
        }

        try {
            jsonArray = new JSONArray(response);
            ArrayList<SubjectsAdapter.Subject> subjects = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                String name = jsonArray.getJSONObject(i).getString("name");
                StringBuilder teacher = new StringBuilder();

                teacher.append(jsonArray.getJSONObject(i).getJSONObject("teacher").getString("name"));
                teacher.append(" ");
                teacher.append(jsonArray.getJSONObject(i).getJSONObject("teacher").getString("surname"));

                SubjectsAdapter.Subject subject = new SubjectsAdapter.Subject(name, teacher.toString());
                subjects.add(subject);

                Log.d("subject", subject.name + " " + subject.teacher);
            }
            SubjectsAdapter adapter = new SubjectsAdapter(requireActivity(), subjects);
            adapter.setListener(listener);
            subjectsRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}