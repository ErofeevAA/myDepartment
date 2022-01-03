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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mydepartment.R;
import com.example.mydepartment.databinding.FragmentSubjectsBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubjectsFragments extends Fragment {

    private FragmentSubjectsBinding binding;

    private LocalStorage storage;

    private ListView subjectsListView;
    private TextView nothingTextView;

    private JSONArray jsonArray;

    private String response = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        storage = new LocalStorage(requireContext());

        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectsListView = binding.subjectsList;
        nothingTextView = binding.textNothing;

        subjectsListView.setOnItemClickListener(itemClickListener);

        new Thread(loadSubjectsRunnable).start();

        return root;
    }

    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = "error";
            try {
                JSONObject object = jsonArray.getJSONObject(position);
                s = object.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("selected", s);
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("handler", response);
            if (msg.what == 0) {
                new FailDialogBuilder(requireActivity(), "Connection failed").show();
            }
            if (response == null) {
                nothingTextView.setVisibility(View.VISIBLE);
                subjectsListView.setVisibility(View.GONE);
                return;
            }
            fillSubjects();
        }
    };

    private final Runnable loadSubjectsRunnable = new Runnable() {
        
        @Override
        public void run() {
            Requests requests = new Requests();
            requests.setToken(storage.getToken());
            requests.subjects();
            response = requests.getResponse();
            Log.d("statusCode", String.valueOf(requests.getStatusCode()));
            handler.sendEmptyMessage(requests.getStatusCode());
        }
    };

    private void fillSubjects() {
        if (response == null) {
            Log.d("fillObject", "null response");
            return;
        }

        try {
            jsonArray = new JSONArray(response);
            String[] subjectsName = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                subjectsName[i] = jsonArray.getJSONObject(i).getString("name");
                Log.d("subjectsName", subjectsName[i]);
            }
            subjectsListView.setAdapter(new ArrayAdapter<>(requireActivity(),
                    R.layout.subjects_list_item, subjectsName
                    ));
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