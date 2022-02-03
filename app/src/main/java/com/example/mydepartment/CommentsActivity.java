package com.example.mydepartment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mydepartment.adapter.CommentAdapter;
import com.example.mydepartment.databinding.ActivityCommentsBinding;
import com.example.mydepartment.dialog.FailDialogBuilder;
import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {
    private ActivityCommentsBinding binding;

    private String subjectID;
    private String sectionID;

    private JSONArray commentJSONArray = null;

    private RecyclerView listCommentsRecyclerView;
    private ProgressBar progressBar;

    private EditText messageEditText;

    private String encodedFile = null;
    private String message = "";
    private String replyCommentID = "";
    private Bitmap avatarBitmap = null;

    private LocalStorage storage;

    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        storage = new LocalStorage(this);

        subjectID = getIntent().getStringExtra("subject_id");
        sectionID = getIntent().getStringExtra("section_id");

        listCommentsRecyclerView = binding.recyclerListComments;
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listCommentsRecyclerView.setLayoutManager(llm);

        ImageView attachFileImageView = binding.imageViewAttach;
        ImageView sendMassageImageView = binding.imageViewSend;

        messageEditText = binding.editTextMessage;

        progressBar = binding.progressBar;

        attachFileImageView.setOnClickListener(v -> attachFile());
        sendMassageImageView.setOnClickListener(v -> sendMessage());

        binding.layoutInfoAttachFile.setOnClickListener(v -> onClickDetach());
        binding.layoutInfoReply.setOnClickListener(v -> onClickDelReply());

        new Thread(getCommentsRunnable).start();
    }

    CommentAdapter.OnItemClickListener itemClickListener = (id, name) -> {
        replyCommentID = id;
        binding.layoutInfoReply.setVisibility(View.VISIBLE);
        binding.textViewReplySend.setText(name);
    };

    CommentAdapter.OnPDFClickListener pdfClickListener = link -> {
        Toast.makeText(getApplicationContext(), "Download File", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            Requests requests = new Requests();
            requests.loadPDF(link);
            runOnUiThread(() -> Toast.makeText(CommentsActivity.this,
                    "Downloaded", Toast.LENGTH_SHORT).show());
        }).start();
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                new FailDialogBuilder(CommentsActivity.this,
                        getString(R.string.alert_connection_failed)).show();
                return;
            }
            if (msg.what == 201) {
                onClickDetach();
                onClickDelReply();
            }
        }
    };

    private final Runnable getCommentsRunnable = () -> {
        Requests requests = new Requests();
        requests.setToken(storage.getToken());
        requests.getComments(subjectID, sectionID);
        avatarBitmap = requests.loadImage(storage.getUrlAvatar());

        Log.d("responseComment", requests.getResponse());

        try {
            commentJSONArray = new JSONArray(requests.getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int status = requests.getStatusCode();
        Log.d("status", String.valueOf(status));

        ArrayList<CommentAdapter.Comment> comments = parseToArrayList(requests);
        runOnUiThread(() -> {
            commentAdapter = new CommentAdapter(CommentsActivity.this, comments);
            commentAdapter.setPDFClickListener(pdfClickListener);
            commentAdapter.setListener(itemClickListener);
            listCommentsRecyclerView.setAdapter(commentAdapter);
            progressBar.setVisibility(View.GONE);
        });
        handler.sendEmptyMessage(status);
    };

    private final Runnable sendCommentRunnable = () -> {
        String data = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", message);
            if (encodedFile != null) {
                jsonObject.put("file", encodedFile);
            }
            if (replyCommentID != null) {
                jsonObject.put("reply", replyCommentID);
            }
            data = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Requests requests = new Requests();
        requests.setToken(storage.getToken());
        Log.d("dataCommentToServer", data);
        requests.sendComments(subjectID, sectionID, data);
        Log.d("codeStatus", String.valueOf(requests.getStatusCode()));
        Log.d("response", requests.getResponse());

        runOnUiThread(() -> {
            String id = "";
            String text = "";
            String author = "";
            String pdfLink = null;
            String replyID = null;
            String replyName = null;
            Bitmap avatar = null;

            try {
                JSONObject object = new JSONObject(requests.getResponse());

                id = object.getString("id");

                text = object.getString("text");

                JSONObject authorObject = object.getJSONObject("author");

                author = authorObject.getString("name");
                author += " ";
                author += authorObject.getString("surname");

                avatar = avatarBitmap;

                pdfLink = object.getString("file");

                // may have exception
                replyID = object.getString("reply_id");
                replyName = object.getString("reply_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CommentAdapter.Comment comment = new CommentAdapter.Comment(author, text);

            if (!pdfLink.equals("null")) {
                comment.linkToPdf = pdfLink;
            }
            comment.avatar = avatar;
            comment.id = id;
            comment.replyID = replyID;
            comment.replyName = replyName;
            commentAdapter.addComment(comment);
            commentAdapter.notifyDataSetChanged();
        });

    };

    private ArrayList<CommentAdapter.Comment> parseToArrayList(Requests requests) {
        if (commentJSONArray == null) {
            return null;
        }

        String id = "";
        String text = "";
        String author = "";
        String pdfLink = "";
        String replyID = null;
        String replyName = null;
        Bitmap avatar = null;

        ArrayList<CommentAdapter.Comment> comments = new ArrayList<>();

        for(int i = 0; i < commentJSONArray.length(); ++i) {
            try {
                JSONObject object = commentJSONArray.getJSONObject(i);

                id = object.getString("id");

                text = object.getString("text");

                JSONObject authorObject = object.getJSONObject("author");

                author = authorObject.getString("name");
                author += " ";
                author += authorObject.getString("surname");

                String link = authorObject.getString("avatar");
                if (!link.equals("null")) {
                    avatar = requests.loadImage(link);
                }

                pdfLink = object.getString("file");

                Log.d("text", text);
                Log.d("author", author);
                Log.d("pdfLink", pdfLink);

                // may have exception
                replyID = object.getString("reply_id");
                replyName = object.getString("reply_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
                CommentAdapter.Comment c = new CommentAdapter.Comment(author, text);
                if (!pdfLink.equals("null")) {
                    c.linkToPdf = pdfLink;
                }
                c.id = id;
                c.name = author;
                c.text = text;
                c.avatar = avatar;
                if (replyID != null) {
                    c.replyID = replyID;
                    c.replyName = replyName;
                    replyID = null;
                    replyName = null;
                }
                comments.add(c);
        }
        return comments;
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();

                        Cursor cursor = getContentResolver().query(uri, null,
                                null, null, null );
                        cursor.moveToFirst();
                        String filePath = cursor.getString(0);
                        cursor.close();

                        Log.d("cursorPath", filePath);

                        try {
                            //del 'raw:' from path
                            File file = new File(filePath.substring(4));

                            double fileSize = (double) file.length() / (1024 * 1024);

                            if (fileSize > 10) {
                                Toast.makeText(getApplicationContext(), R.string.too_large_file, Toast.LENGTH_SHORT).show();
                            }

                            FileInputStream fileInputStream = new FileInputStream(file);

                            byte[] bytes = new byte[(int) file.length()];
                            fileInputStream.read(bytes);

                            encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
                            if (encodedFile != null) {
                                binding.layoutInfoAttachFile.setVisibility(View.VISIBLE);
                            }
                            Log.d("encodedFile", encodedFile);
                            Log.d("fileName", file.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void attachFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        someActivityResultLauncher.launch(intent);
    }

    private void sendMessage() {
        message = messageEditText.getText().toString();
        if (message.equals("")) {
            Toast.makeText(getApplicationContext(), "Empty message", Toast.LENGTH_SHORT).show();
            return;
        }
        hiddenKeyboard();
        new Thread(sendCommentRunnable).start();
    }

    private void onClickDetach() {
        binding.layoutInfoAttachFile.setVisibility(View.GONE);
        encodedFile = null;
    }

    private void onClickDelReply() {
        binding.layoutInfoReply.setVisibility(View.GONE);
        replyCommentID = null;
    }

    private void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            sendMessage();
            hiddenKeyboard();

        }
        return super.onKeyUp(keyCode, event);
    }
}