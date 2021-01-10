package com.ruizhou.blueterminal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Graphing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);

        String filename = getIntent().getStringExtra("filename");

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




        GraphView graph = (GraphView) findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>();

        if (TextUtils.isEmpty(file_data)) {
//            TextView dataView = (TextView) findViewById(R.id.notification);
//            dataView.setText("No data to graph");
            series.appendData(new DataPoint(0, 0.0), true, 1);
            series2.appendData(new DataPoint(0, 0.0), true, 1);
            graph.addSeries(series);
        }
        else {

            String[] separated = file_data.split("\n");
            for (int i = 0; i < separated.length; i++) {
                String line = separated[i].trim();

                String[] sep = line.split(",");
                String gas = sep[1].trim();
                String distance = sep[2].trim();

                int x = Integer.parseInt(sep[0].trim());
                Double g = Double.parseDouble(gas);
                Double d = Double.parseDouble(distance);
                series.appendData(new DataPoint(x, g), true, separated.length);
                series2.appendData(new DataPoint(x, d), true, separated.length);
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
    }
}