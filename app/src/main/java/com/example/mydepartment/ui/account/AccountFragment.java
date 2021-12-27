package com.example.mydepartment.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mydepartment.LoginActivity;
import com.example.mydepartment.databinding.FragmentAccountBinding;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private LocalStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        storage = new LocalStorage(requireContext());

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        fillInfo();
        binding.buttonLogout.setOnClickListener(v -> logout());
        return binding.getRoot();
    }

    private void fillInfo() {
        String fullName = storage.getName() + " " + storage.getSurname();
        binding.textViewFullName.setText(fullName);
        binding.textViewEmail.setText(storage.getEmail());
        binding.textViewRole.setText(storage.getRole());

        String group = storage.getGroup();
        if (group != null) {
            binding.headerGroup.setVisibility(View.VISIBLE);
            binding.textViewGroup.setVisibility(View.VISIBLE);
            binding.textViewGroup.setText(group);
        }

    }

    private void logout() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Requests requests = new Requests();
                requests.setToken(storage.getToken());
                requests.logout();

                requireActivity().runOnUiThread(() -> {
                    int code = requests.getStatusCode();
                    Log.d("statusCode", String.valueOf(code));
                    if (code != 200) {
                        Toast.makeText(requireActivity(), "Unexpected error", Toast.LENGTH_LONG).show();
                    }
                    storage.clear();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}