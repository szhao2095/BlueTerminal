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

        Context context = getApplicationContext();

        String filename = "test.txt";

        try {
            File.createTempFile(filename, null, context.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        File cacheFile = new File(context.getCacheDir(), filename);

        String fileContents = "0, 280, 6.09\n"
                            + "1, 277, 5.91\n"
                            + "2, 269, 8.56\n"
                            + "3, 255, 6.46\n"
                            + "4, 242, 6.50\n"
                            + "5, 224, 6.61\n"
                            + "6, 214, 6.56\n"
                            + "7, 202, 9.31\n"
                            + "8, 192, 15.54\n"
                            + "9, 181, 10.20\n"
                            + "10, 172, 5.39\n"
                            + "11, 166, 6.18\n"
                            + "12, 159, 6.52\n"
                            + "13, 153, 6.27\n"
                            + "14, 150, 5.36\n"
                            + "15, 145, 5.61\n"
                            + "16, 141, 5.67\n"
                            + "17, 137, 6.16\n"
                            + "18, 134, 5.83\n"
                            + "19, 132, 6.87";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(fileContents.getBytes()); // Need to convert string to bytes
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

        // Read the file
        FileInputStream fis = null;
        String title_name = null;

        try {
            fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            title_name = sb.toString();
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

        GraphView graph = (GraphView) findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>();

        String[] separated = title_name.split("\n");
        for (int i = 0; i < 20; i++) {
            String line = separated[i].trim();

            String[] sep = line.split(",");
            String gas = sep[1].trim();
            String distance = sep[2].trim();

            int x = Integer.parseInt(sep[0].trim());
            Double g = Double.parseDouble(gas);
            Double d = Double.parseDouble(distance);
            series.appendData(new DataPoint(x, g), true, 20);
            series2.appendData(new DataPoint(x, d), true, 20);
        }

        series.setTitle("Gas Value (ADC)");
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        graph.addSeries(series);

        // set second scale
        series2.setTitle("Distance (cm)");
        series2.setColor(Color.RED);
        series2.setDrawDataPoints(true);
        series2.setDataPointsRadius(10);
        series2.setThickness(8);

        graph.getSecondScale().addSeries(series2);
        // the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(20);
        graph.getSecondScale().setVerticalAxisTitle("Distance (cm)");
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);


        graph.getLegendRenderer().setVisible(true);

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(true);

        // activate vertical scrolling
        graph.getViewport().setScrollableY(true);

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (s)");
        gridLabel.setVerticalAxisTitle("Gas Value (ADC)");
    }
    public void openData() {
        Intent intent = new Intent(this, DisplayData.class);
        startActivity(intent);
    }
}