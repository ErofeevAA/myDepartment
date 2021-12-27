package com.example.mydepartment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydepartment.databinding.ActivityLoginBinding;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.signIn.setOnClickListener(v -> {
            if (isCorrectInput()) {
                sendLogin();
            } else {
                alertFail("Incorrect login or/and password");
            }
        });

        binding.noAccount.setOnClickListener(v -> {
            Intent i = new Intent(this, RegistrationActivity.class);
            startActivity(i);
        });
    }

    private boolean isCorrectInput() {
        int lEmail = Objects.requireNonNull(binding.email.getText()).length();
        int lPassword = Objects.requireNonNull(binding.password.getText()).length();
        return !(lEmail == 0 && lPassword == 0);
    }

    private void sendLogin() {
        JSONObject params = new JSONObject();
        try {
            params.put("email", Objects.requireNonNull(binding.email.getText()).toString());
            params.put("password", Objects.requireNonNull(binding.password.getText()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = params.toString();
        Log.d("dataLogin", data);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Requests requests = new Requests();
                requests.login(data);

                runOnUiThread(() -> {
                    int code = requests.getStatusCode();
                    if(code == 0) {
                        alertFail("Connection failed");
                        return;
                    }

                    Log.d("statusCode", String.valueOf(code));
                    Log.d("message", requests.getResponse());

                    if (code == 422) {
                        alertFail("Incorrect login or/and password");
                    } else if (code == 423) {
                        alertFail("Your account is not confirmed");
                    } else if (code == 201) {
                        saveUser(requests.getResponse());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        alertFail("Unexpected error");
                    }
                });
            }
        }.start();
    }

    private void saveUser(String response) {
        try {
            JSONObject params = new JSONObject(response);

            Log.d("token", params.getString("token"));

            LocalStorage storage = new LocalStorage(this);
            storage.setToken(params.getString("token"));
            storage.setName(params.getJSONObject("user").getString("name"));
            storage.setSurname(params.getJSONObject("user").getString("surname"));
            storage.setEmail(params.getJSONObject("user").getString("email"));

            String role = params.getJSONObject("user").getString("role");
            storage.setRole(role);
            if (role.equals("student")) {
                storage.setGroup(params.getJSONObject("user").getString("group"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertFail(String alert) {
        new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setMessage(alert)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}