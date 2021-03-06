package com.ruizhou.blueterminal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ruizhou.blueterminal.Activity.DisplayData;
import com.ruizhou.blueterminal.Activity.Graphing;
import com.ruizhou.blueterminal.R;

public class GraphData extends AppCompatActivity {

    private Button displayData;
    private Button graphing;
    private Button upload;
    private String filename;
    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_data);

//        filename = getIntent().getStringExtra("filename");
        Bundle extras = getIntent().getExtras();
        filename = extras.getString("filename");
        filepath = extras.getString("filepath");
        Log.d("SANITY", "path: " + filepath);

        displayData = (Button) findViewById(R.id.button_data);
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openData(filename);
            }
        });

        graphing = (Button) findViewById(R.id.button_graphing);
        graphing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphing(filename);
            }
        });

        upload = (Button) findViewById(R.id.button_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload file
            }
        });

    }
    public void openData(String filename) {
        Intent intent = new Intent(this, DisplayData.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }

    public void openGraphing(String filename) {
        Intent intent = new Intent(this, Graphing.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }
}