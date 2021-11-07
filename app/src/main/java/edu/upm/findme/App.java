package edu.upm.findme;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import edu.upm.findme.utility.MqttTalker;
import edu.upm.findme.utility.StepSensor;
import edu.upm.findme.utility.UserInfoManager;

public class App extends Application implements AppEvent.Observer, StepSensor.SensorInterface {

    public UserInfoManager userInfo;
    public MqttTalker mqtt;
    public StepSensor stepSensor;

    boolean hasBeenInitialized;
    MortalObserver currentObserver = null;

    public App init() {
        if (!hasBeenInitialized) {
            userInfo = new UserInfoManager(this);
            mqtt = new MqttTalker(this, this, userInfo.getUserId());
            stepSensor = new StepSensor(this, this);
            hasBeenInitialized = true;
        }
        return this;
    }

    public App initWithObserver(MortalObserver observer) {
        addObserver(observer);
        return init();
    }

    public void addObserver(MortalObserver observer) {
        observer.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_RESUME)
                currentObserver = observer;
            else if (event == Lifecycle.Event.ON_PAUSE)
                currentObserver = null;
        });
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (currentObserver != null)
            currentObserver.onGlobalEvent(e);
    }

    @Override
    public int loadStepCount() {
        return userInfo.getTotalSteps();
    }

    @Override
    public void stepCountChanged(int stepsTaken) {
        mqtt.publishStepsTaken(stepsTaken);
        onGlobalEvent(AppEvent.Type.STEP_TAKEN_BY_USER);
        userInfo.setTotalSteps(stepsTaken);
    }

    public interface MortalObserver extends AppEvent.Observer, LifecycleOwner {
    }
}
