package com.ruizhou.blueterminal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ruizhou.blueterminal.Activity.DisplayData;
import com.ruizhou.blueterminal.Activity.Graphing;
import com.ruizhou.blueterminal.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphData extends AppCompatActivity {

    private Button displayData;
    private Button graphing;
    private Button upload;
    private String filename;
    private String filepath;
    private String sensor_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_data);

//        filename = getIntent().getStringExtra("filename");
        Bundle extras = getIntent().getExtras();
        filename = extras.getString("filename");
        filepath = extras.getString("filepath");
        sensor_name = extras.getString("sensor_name");
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
                // Create a copy of the file in EXTERNAL STORAGE
                // Read the file
                FileInputStream fis = null;
                String file_data = null;

                try {
                    fis = openFileInput(filename);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();

                    String text;
                    while ((text = br.readLine()) != null) {
                        Pattern endIndicator = Pattern.compile("END");
                        Matcher m = endIndicator.matcher(text);
                        if (m.find()) {
                            continue;
                        }
                        sb.append(text).append("\n");
                    }

                    file_data = sb.toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d("SANITYCONTENT", file_data);
//                boolean check = isExternalStorageWritable();
                writeFile(file_data);
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

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("WRITABLE", "It is writable");
            return true;
        } else {
            return false;
        }
    }
    public void writeFile(String file_data) {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String file_name = sensor_name + "_" + filename;
            File textFile = new File(Environment.getExternalStorageDirectory(), file_name);
            // Write to external storage file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(textFile);
                fos.write(file_data.getBytes(), 0, file_data.length());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { // This code is executed even if exception is thrown
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}