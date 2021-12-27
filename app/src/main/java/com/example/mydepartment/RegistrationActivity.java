package com.example.mydepartment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mydepartment.databinding.ActivityRegistrationBinding;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private final Map<String, String> role = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        role.put(getString(R.string.choose_role_teacher), "teacher");
        role.put(getString(R.string.choose_role_student), "student");

        ActivityRegistrationBinding binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Spinner spinnerRole = binding.role;
        spinnerRole.setAdapter(getRoleAdapter());

        Spinner spinnerGroup = binding.group;
        spinnerGroup.setAdapter(getGroupAdapter());
    }

    private ArrayAdapter<String> getRoleAdapter() {
        String[] plug = {
                getString(R.string.choose_role),
                getString(R.string.choose_role_teacher),
                getString(R.string.choose_role_student)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plug);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        return adapter;
    }

    private ArrayAdapter<String> getGroupAdapter() {
        String[] plug = {getString(R.string.choose_group), "DarkTurquoise", "DarkGray", "Chocolate", "PaleGoldenRod", "Pink"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plug);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        return adapter;
    }
}