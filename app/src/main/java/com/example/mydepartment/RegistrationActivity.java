package com.example.mydepartment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydepartment.databinding.ActivityRegistrationBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class RegistrationActivity extends AppCompatActivity {
    private JSONArray groupsJSONArray;
    private int chosen_group = -1;
    private String data;
    private String encodeImage = null;

    private TextView textViewStateAvatar;

    private ImageView imageAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRegistrationBinding binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        new Thread(loadGroupRunnable).start();

        Spinner spinnerGroup = binding.group;

        TextView textViewName = binding.textViewName;
        TextView textViewSurname = binding.textViewSurname;
        TextView textViewEmail = binding.textInputEmail;
        TextView textViewPassword = binding.textViewPassword;

        imageAvatar = binding.imageViewAvatar;

        textViewStateAvatar = binding.textViewStateAvatar;

        LinearLayout linearLayout = binding.layoutAddAvatar;
        linearLayout.setOnClickListener(addImage);

        Button buttonSignUp = binding.buttonSignUp;
        buttonSignUp.setOnClickListener(v -> {
            String name = textViewName.getText().toString();
            String surname = textViewSurname.getText().toString();
            String email = textViewEmail.getText().toString();
            String password = textViewPassword.getText().toString();
            if (password.length() < 10 || password.length() > 30) {
                new FailDialogBuilder(RegistrationActivity.this,
                        getString(R.string.alert_wrong_password_length)).show();
                return;
            }
            if (chosen_group > -1 && !name.equals("") && !surname.equals("") &&
                    !email.equals("")) {
                try {
                    String group = groupsJSONArray.getJSONObject(chosen_group).getString("name");
                    JSONObject object = new JSONObject();
                    object.put("role", "student");
                    object.put("email", email);
                    object.put("name", name);
                    object.put("surname", surname);
                    object.put("password", password);
                    if (encodeImage != null) {
                        object.put("avatar", encodeImage);
                    }
                    object.put("group", group);
                    data = object.toString();
                    Log.d("Data", data);
                    new Thread(sendDataRunnable).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                new FailDialogBuilder(this, getString(R.string.alert_fill_all_field)).show();
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosen_group = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        imageAvatar.setImageURI(uri);
                        BitmapDrawable drawable = (BitmapDrawable) imageAvatar.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, b);
                        encodeImage = Base64.encodeToString(b.toByteArray(), Base64.DEFAULT);
                        Log.d("encodeImage", encodeImage);

                        textViewStateAvatar.setText(R.string.image_has_been_added);
                    }
                }
            });

    private final View.OnClickListener addImage = v -> {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == -200) {
                Spinner s = findViewById(R.id.group);
                s.setAdapter(getGroupAdapter());
                return;
            }
            if (msg.what == 0) {
                new FailDialogBuilder(RegistrationActivity.this,
                        getString(R.string.alert_connection_failed)).show();
                return;
            }
            if (msg.what == 200) {
                Toast.makeText(getApplicationContext(), R.string.toast_reg_success, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            if (msg.what == 422) {
                new FailDialogBuilder(RegistrationActivity.this,
                        getString(R.string.alert_check_field)).show();
            }
        }
    };

    private final Runnable loadGroupRunnable = () -> {
        Requests requests = new Requests();
        requests.groups();
        Log.d("response", requests.getResponse());
        if (requests.getStatusCode() == 200) {
            try {
                groupsJSONArray = new JSONArray(requests.getResponse());
                handler.sendEmptyMessage(-200);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        runOnUiThread(() -> {
            new FailDialogBuilder(RegistrationActivity.this,
                    getString(R.string.alert_connection_failed));
            finish();
        });
    };

    private final Runnable sendDataRunnable = () -> {
        Requests requests = new Requests();
        requests.register(data);
        Log.d("response", requests.getResponse());
        Log.d("Status", String.valueOf(requests.getStatusCode()));
        handler.sendEmptyMessage(requests.getStatusCode());
    };

    private ArrayAdapter<String> getGroupAdapter() {
        String[] array = new String[groupsJSONArray.length() + 1];
        array[0] = getString(R.string.choose_group);
        for (int i = 0; i < groupsJSONArray.length(); ++i) {
            try {
                array[i + 1] = groupsJSONArray.getJSONObject(i).getString("short_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        return adapter;
    }
}