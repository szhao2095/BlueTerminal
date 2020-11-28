package com.ruizhou.blueterminal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class DisplayData extends AppCompatActivity {
//    private TextView dataView = (TextView) findViewById(R.id.dataTextView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

//        String datatext = "Test hello zzzzzzzzzzzzzz";
//        TextView dataView = (TextView) findViewById(R.id.dataTextView);
//        dataView.setText(datatext);

        String filename = "data.txt";

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

        TextView dataView = (TextView) findViewById(R.id.dataTextView);
        dataView.setText(file_data);
//        System.out.println(file_data);
    }
}