package com.example.mydepartment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mydepartment.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistrationBinding binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Spinner spinnerRole = binding.role;
        spinnerRole.setAdapter(getRoleAdapter());

        Spinner spinnerGroup = binding.group;
        spinnerGroup.setAdapter(getGroupAdapter());
    }

    private ArrayAdapter<String> getRoleAdapter() {
        String[] plug = {"В темнице", "Тёмной", "В час ночной"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plug);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        return adapter;
    }

    private ArrayAdapter<String> getGroupAdapter() {
        String[] plug = {"Стоял", "С немытой", "Головой"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, plug);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        return adapter;
    }
}