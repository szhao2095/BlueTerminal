package com.ruizhou.blueterminal;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;

public class FileList extends AppCompatActivity {

    private Button file1;
    private String filename1;

    private Button file2;
    private String filename2;

    private BLE_Service ble;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);


        ble = MainActivity.ble;
        context = MainActivity.context;

//        filename1 = getIntent().getStringExtra("filename");
        filename1 = "dfile3.txt";
        filename2 = "dfile2.txt";

        file1 = (Button) findViewById(R.id.button_file_data);
        file1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                ble.setFileListName(filename1);
                ble.setFile(context); // Delete file if it exists and create new file
                try {
                    String command = filename1 + "#";
                    ble.writeData(command);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                openFileData(filename1);

            }
        });

        file2 = (Button) findViewById(R.id.button_file_data2);
        file2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                ble.setFileListName(filename2);
                ble.setFile(context); // Delete file if it exists and create new file
                try {
                    String command = filename2 + "#";
                    ble.writeData(command);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                openFileData(filename2);

            }
        });
    }

    public void openFileData(String filename) {
        Intent intent = new Intent(this, GraphData.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }
}