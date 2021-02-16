package com.ruizhou.blueterminal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.os.Build;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruizhou.blueterminal.BLE_Service;
import com.ruizhou.blueterminal.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class FileList extends AppCompatActivity {

    private Button dump;



    private BLE_Service ble;
    private Context context;
    private String read_data;
    private HashMap<String, Integer> cachedFiles;
    private String[] file_names;

    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        layout = findViewById(R.id.linearlayoutid);


        ble = MainActivity.ble;
        context = MainActivity.context;
        read_data = MainActivity.read_data;
        cachedFiles = MainActivity.cachedFiles;

        // Read filelist.txt file and store file names into file_names list
        FileInputStream fis = null;
        String file_data = null;
        try {
            fis = new FileInputStream(ble.anchorFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            file_data = sb.toString();
            isr.close();
            br.close();
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


        // Parse and clean read_data
        // Create dynamic buttons
        file_names = file_data.split("\n");
        for (int i = 0; i < file_names.length; i++) {
            file_names[i] = file_names[i].trim();
            final String filename = file_names[i];
            final Button button = new Button(this);
//            button.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            button.setId(i);
            button.setText(filename);
            button.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    // If file is not cached, create file and insert filename into hashmap
                    if (!cachedFiles.containsKey(filename)) {
                        ble.setFileListName(filename);
                        ble.setFile(context); // Delete file if it exists and create new file
                        try {
                            String command = filename + "#";
                            ble.writeData(command);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        // Insert into hashmap
                        cachedFiles.put(filename, 1);
                    }
                    openFileData(filename);
                }
            });

            layout.addView(button);
        }






        // Dump all files that have not been dumped
        dump = (Button) findViewById(R.id.button_file_dump);
        dump.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                for (int i = 0; i < file_names.length; i++) {
                    String filename = file_names[i];
                    Log.d("FILENAMEEEE", "filename: " + filename);
                    // If file is not cached, create file and insert filename into hashmap
                    if (!cachedFiles.containsKey(filename)) {
                        ble.setFileListName(filename);
                        ble.setFile(context); // Delete file if it exists and create new file
                        try {
                            String command = filename + "#";
                            Log.d("COMMANDDDD", "command: " + command);
                            ble.writeData(command);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        // Insert into hashmap
                        cachedFiles.put(filename, 1);

                        // Wait for onCharacteristicChanged to finish running
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void openFileData(String filename) {
        Intent intent = new Intent(this, GraphData.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
    }
}