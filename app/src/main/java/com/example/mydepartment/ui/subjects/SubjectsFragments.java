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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mydepartment.LoginActivity;
import com.example.mydepartment.R;
import com.example.mydepartment.databinding.FragmentSubjectsBinding;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectsFragments extends Fragment {

    private FragmentSubjectsBinding binding;

    private LocalStorage storage;

    private ListView subjectsListView;
    private TextView nothingTextView;

    private ArrayList<String> subjectArrayList;

    private String response = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        storage = new LocalStorage(requireContext());

        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectsListView = binding.subjectsList;
        nothingTextView = binding.textNothing;

        new Thread(loadSubjectsRunnable).start();

        return root;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("handler", response);
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
            handler.sendEmptyMessage(0);
        }
    };

    private void fillSubjects() {
        if (response == null) {
            Log.d("fillObject", "null response");
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(response);
            String[] subjectsName = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                subjectsName[i] = new JSONObject(jsonArray.getString(i)).getString("name");
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