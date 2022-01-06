package com.example.mydepartment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydepartment.databinding.ActivityLoginBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private String response = null;

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

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                alertFail(getString(R.string.alert_connection_failed));
                return;
            }
            if (msg.what == 422) {
                alertFail(getString(R.string.alert_incorrect_login_or_password));
                return;
            }
            if (msg.what == 423) {
                alertFail("Your account is not confirmed");
                return;
            }
            if (msg.what == 201) {
                saveUser(response);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            alertFail("Unexpected error");
        }
    };

    private boolean isCorrectInput() {
        int lEmail = Objects.requireNonNull(binding.textInputEmail.getText()).length();
        int lPassword = Objects.requireNonNull(binding.textViewPassword.getText()).length();
        return !(lEmail == 0 && lPassword == 0);
    }

    private void sendLogin() {
        JSONObject params = new JSONObject();
        try {
            params.put("email", Objects.requireNonNull(binding.textInputEmail.getText()).toString());
            params.put("password", Objects.requireNonNull(binding.textViewPassword.getText()).toString());
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

                int code = requests.getStatusCode();
                response = requests.getResponse();
                Log.d("statusCode", String.valueOf(code));
                Log.d("message", response);
                handler.sendEmptyMessage(code);
            }
        }.start();
    }

    private void saveUser(String response) {
        try {
            JSONObject params = new JSONObject(response);

            Log.d("token", params.getString("token"));

            LocalStorage storage = new LocalStorage(this);
            storage.setToken(params.getString("token"));

            JSONObject userObject = params.getJSONObject("user");

            storage.setName(userObject.getString("name"));
            storage.setSurname(userObject.getString("surname"));
            storage.setEmail(userObject.getString("email"));

            String role = userObject.getString("role");
            storage.setRole(role);
            if (role.equals("student")) {
                storage.setGroup(userObject.getString("group"));
            }

            String urlImg = userObject.getString("avatar");
            if (!urlImg.equals("null")) {
                storage.setUrlAvatar(urlImg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertFail(String alert) {
        new FailDialogBuilder(this, alert).show();
    }
}
