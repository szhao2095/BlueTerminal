package com.ruizhou.blueterminal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

public class FileList extends AppCompatActivity {

    private Button file1;
    private String filename1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        filename1 = getIntent().getStringExtra("filename");

        file1 = (Button) findViewById(R.id.button_file_data);
        file1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileData(filename1);

            }
        });
    }

    public void openFileData(String filename) {
        Intent intent = new Intent(this, GraphData.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }
}