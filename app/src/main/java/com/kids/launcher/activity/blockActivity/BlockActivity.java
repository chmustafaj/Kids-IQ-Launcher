package com.kids.launcher.activity.blockActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kids.launcher.R;

public class BlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        String getIntent = getIntent().getStringExtra("ID");

    }
}