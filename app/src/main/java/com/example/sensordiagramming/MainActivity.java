package com.example.sensordiagramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView sensorDataX;
    private TextView sensorDataY;
    private TextView sensorDataZ;
    private LineChart chart;
    private List<Entry> xValues = new ArrayList<>();
    private List<Entry> yValues = new ArrayList<>();
    private List<Entry> zValues = new ArrayList<>();
    private int maxEntries = 1;
    private int time = 0;
    boolean running = true;
    private int samplingRate = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button start = (Button) findViewById(R.id.button_start);
        final Button stop = (Button) findViewById(R.id.button_stop);
        final Button submit = (Button) findViewById(R.id.submit);
        final EditText samplingRateInput = (EditText) findViewById(R.id.sampling_input);
        final EditText maxEntriesInput = (EditText) findViewById(R.id.max_entries_input);
        sensorDataX = (TextView) findViewById(R.id.textView_x);
        sensorDataY = (TextView) findViewById(R.id.textView_y);
        sensorDataZ = (TextView) findViewById(R.id.textView_z);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, samplingRate);
        chart = (LineChart) findViewById(R.id.lineChart);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View start) {
                running = true;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View stop) {
                running = false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View submit) {
                String entries = maxEntriesInput.getText().toString();
                maxEntries = Integer.parseInt(entries);
                String sampling = samplingRateInput.getText().toString();
                samplingRate = Integer.parseInt(sampling);

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            maxEntries = maxEntries;
            samplingRate = samplingRate;
            time ++;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            sensorDataX.setText("X: " + x);
            sensorDataY.setText("Y: " + y);
            sensorDataZ.setText("Z: " + z);

            xValues.add(new Entry(time, x));
            yValues.add(new Entry(time, y));
            zValues.add(new Entry(time, z));

            if (xValues.size() > maxEntries) {
                xValues.remove(0);
                yValues.remove(0);
                zValues.remove(0);
            }

            LineDataSet xDataSet = new LineDataSet(xValues, "X-Achse");
            LineDataSet yDataSet = new LineDataSet(yValues, "Y-Achse");
            LineDataSet zDataSet = new LineDataSet(zValues, "Z-Achse");

            xDataSet.setColor(Color.RED);
            yDataSet.setColor(Color.GREEN);
            zDataSet.setColor(Color.BLUE);

            xDataSet.setDrawCircles(false);
            yDataSet.setDrawCircles(false);
            zDataSet.setDrawCircles(false);

            xDataSet.setLineWidth(3f);
            yDataSet.setLineWidth(3f);
            zDataSet.setLineWidth(3f);

            LineData data = new LineData(xDataSet, yDataSet, zDataSet);
            chart.setData(data);
            chart.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nicht benötigt
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}