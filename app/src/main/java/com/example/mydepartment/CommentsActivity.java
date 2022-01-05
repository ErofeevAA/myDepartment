package com.example.mydepartment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CommentsActivity extends AppCompatActivity {
    private String subjectID;
    private String sectionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        subjectID = getIntent().getStringExtra("subject_id");
        sectionID = getIntent().getStringExtra("section_id");
    }
}