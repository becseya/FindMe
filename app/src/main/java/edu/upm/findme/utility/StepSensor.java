package edu.upm.findme.utility;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepSensor implements SensorEventListener {

    final SensorManager sensorManager;
    final Sensor mySensor;
    final SensorInterface handler;
    int totalStepsTaken;
    boolean running;

    public StepSensor(Context appContext, SensorInterface handler) {
        this.sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
        this.handler = handler;
        this.mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public int getTotalStepCount() {
        return totalStepsTaken;
    }

    public void start() {
        if (!running) {
            totalStepsTaken = handler.loadStepCount();
            sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
            running = true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        totalStepsTaken++;
        handler.stepCountChanged(totalStepsTaken);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public interface SensorInterface {
        int loadStepCount();

        void stepCountChanged(int stepsTaken);
    }
}
