package edu.upm.findme.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import edu.upm.findme.App;
import edu.upm.findme.R;

public class StepsActivity extends AppCompatActivity {

    App app;
    List<BarEntry> entries;
    ArrayList<String> labels;
    BarDataSet dataSetSteps;
    BarData chartData;
    BarChart chartSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        app = ((App) getApplicationContext()).init();

        entries = new ArrayList<>();
        entries.add(new BarEntry(1, 220));
        entries.add(new BarEntry(2, 110));
        entries.add(new BarEntry(3, 40));

        labels = new ArrayList<>();
        labels.add("Y axis");
        labels.add("Maryam");
        labels.add("Ana");
        labels.add("Ákos");

        chartSteps = (BarChart) findViewById(R.id.chartSteps);
        dataSetSteps = new BarDataSet(entries, "Steps");
        chartData = new BarData(dataSetSteps);
        chartSteps.setData(chartData);
        chartSteps.setDescription(null);

        XAxis xAxis = chartSteps.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
