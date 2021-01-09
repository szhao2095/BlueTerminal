package com.ruizhou.blueterminal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphData extends AppCompatActivity {

    private Button displayData;
    private Button graphing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_data);

        displayData = (Button) findViewById(R.id.button_data);
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openData();
            }
        });

        graphing = (Button) findViewById(R.id.button_graphing);
        graphing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphing();
            }
        });

    }
    public void openData() {
        Intent intent = new Intent(this, DisplayData.class);
        startActivity(intent);
    }

    public void openGraphing() {
        Intent intent = new Intent(this, Graphing.class);
        startActivity(intent);
    }
}