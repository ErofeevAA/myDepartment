package com.example.mydepartment.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mydepartment.R;
import com.example.mydepartment.adapter.NotificationAdapter;
import com.example.mydepartment.databinding.FragmentNotificationsBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private LocalStorage storage;

    private String textResponse = "";

    //private NotificationAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        storage = new LocalStorage(requireContext());

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerListNotifications.setLayoutManager(llm);

        View root = binding.getRoot();
        new Thread(loadNotifications).start();
        return root;
    }

    private final NotificationAdapter.OnItemButtonClickListener buttonClickListener = (n, position)
            -> new Thread(() -> {
        Requests requests = new Requests();
        requests.setToken(storage.getToken());
        for (int i = 0; i < n.ids.size(); ++i) {
            requests.sendNotificationsID(n.ids.get(i));
        }
        requireActivity().runOnUiThread(() -> {
            NotificationAdapter adapter = (NotificationAdapter) binding.recyclerListNotifications.getAdapter();
            assert adapter != null;
            adapter.remove(position);
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() == 0) {
                binding.recyclerListNotifications.setVisibility(View.GONE);
                binding.textViewNoNotifications.setVisibility(View.VISIBLE);
            }
        });
    }).start();

    private final Handler handlerNotifications = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                new FailDialogBuilder(requireActivity(),
                        getString(R.string.alert_connection_failed));
                return;
            }
            if (msg.what == 200) {
                if (textResponse.equals("[]")) {
                    binding.recyclerListNotifications.setVisibility(View.GONE);
                    binding.textViewNoNotifications.setVisibility(View.VISIBLE);
                    return;
                }
                fillRecycler();
            }

        }
    };

    Runnable loadNotifications = () -> {
        Requests requests = new Requests();
        requests.setToken(storage.getToken());
        requests.getNotifications();
        Log.d("status", String.valueOf(requests.getStatusCode()));
        if (requests.getResponse() != null) {
            Log.d("response", requests.getResponse());
            textResponse = requests.getResponse();
        }
        handlerNotifications.sendEmptyMessage(requests.getStatusCode());
    };

    private void fillRecycler() {
        ArrayList<NotificationAdapter.Notification> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(textResponse);

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject object = jsonArray.getJSONObject(i);
                String text = object.getJSONObject("notification").getString("text");

                JSONArray idsJsonArray = object.getJSONArray("ids");
                ArrayList<String> ids = new ArrayList<>();
                for (int j = 0; j < idsJsonArray.length(); ++j) {
                    ids.add(idsJsonArray.getString(j));
                }

                NotificationAdapter.Notification n = new NotificationAdapter.Notification();
                n.ids = ids;
                n.text = text;
                arrayList.add(n);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationAdapter adapter = new NotificationAdapter(getContext(), arrayList);
        adapter.setListener(buttonClickListener);
        binding.recyclerListNotifications.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}