package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.User;

public class StepsActivity extends AppCompatActivity implements App.MortalObserver {

    private static final int MAX_NUMBER_OF_TOP_USERS = 3;

    App app;
    List<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    BarDataSet dataSetSteps;
    BarData chartData;
    BarChart chartSteps;
    int stepsTaken = 0;
    TextView stepsTakenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        app = ((App) getApplicationContext()).initWithObserver(this);

        stepsTakenText = findViewById(R.id.lblStepsTaken);

        chartSteps = (BarChart) findViewById(R.id.chartSteps);
        dataSetSteps = new BarDataSet(entries, "Steps");
        chartData = new BarData(dataSetSteps);
        chartSteps.setData(chartData);
        chartSteps.setDescription(null);

        XAxis xAxis = chartSteps.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chartSteps.getAxisLeft().setAxisMinimum(0.f);
        chartSteps.getAxisRight().setAxisMinimum(0.f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    void updateUI() {
        calculateTopUsersAndStepsTaken();
        dataSetSteps.notifyDataSetChanged();
        chartData.notifyDataChanged();
        chartSteps.notifyDataSetChanged();
        chartSteps.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartSteps.invalidate();

        String stepsTakenString = getString(R.string.lbl_steps_taken);
        stepsTakenString = stepsTakenString.replace("N", String.valueOf(stepsTaken));
        stepsTakenText.setText(stepsTakenString);
    }

    void updateTopUsers(List<TopEntry> list, TopEntry candidate) {
        if (list.size() == 0)
            list.add(candidate);
        else {
            boolean notAddedYet = true;

            for (int i = 0; i < list.size(); i++) {
                if (candidate.steps > list.get(i).steps) {
                    list.add(i, candidate);
                    notAddedYet = false;
                    break;
                }
            }
            if (notAddedYet && (list.size() < MAX_NUMBER_OF_TOP_USERS))
                list.add(candidate);
            else if (list.size() > MAX_NUMBER_OF_TOP_USERS)
                list.remove(list.size() - 1);
        }
    }

    void calculateTopUsersAndStepsTaken() {
        List<TopEntry> top3users = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : app.mqtt.getSteps().entrySet()) {
            User user = User.getById(app.users, entry.getKey());

            if (user != null)
                updateTopUsers(top3users, new TopEntry(entry.getKey(), entry.getValue(), user.getName()));

            if (entry.getKey() == app.userInfo.getUserId())
                stepsTaken = entry.getValue();
        }

        labels.clear();
        entries.clear();
        for (int i = 0; i < top3users.size(); i++) {
            entries.add(new BarEntry(i, top3users.get(i).steps));
            labels.add(top3users.get(i).name);
        }
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (e.equals(AppEvent.Type.STEP_SCORES_CHANGED))
            updateUI();
    }

    private static class TopEntry {
        public int id;
        public int steps;
        public String name;

        public TopEntry(Integer id, Integer steps, String name) {
            this.id = id;
            this.steps = steps;
            this.name = name;
        }
    }
}
