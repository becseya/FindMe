package edu.upm.findme;

import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import edu.upm.findme.utility.MqttTalker;
import edu.upm.findme.utility.UserInfoManager;

public class App extends Application implements AppEvent.Observer {

    public UserInfoManager userInfo;
    public MqttTalker mqtt;

    boolean hasBeenInitialized;
    List<MortalObserver> observers = new ArrayList<>();

    public App init() {
        if (!hasBeenInitialized) {
            userInfo = new UserInfoManager(this);
            mqtt = new MqttTalker(this, this, userInfo.getUserId());
            hasBeenInitialized = true;
        }
        return this;
    }

    public App initWithObserver(MortalObserver observer) {
        addObserver(observer);
        return init();
    }

    public void addObserver(MortalObserver observer) {
        if (!isObserverAlreadyAdded(observer)) {
            observer.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    removeObserver(observer);
                }
            });
            observers.add(observer);
        }
    }

    public void removeObserver(MortalObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        for (MortalObserver o : observers)
            o.onGlobalEvent(e);
    }

    boolean isObserverAlreadyAdded(MortalObserver observer) {
        for (MortalObserver o : observers) {
            if (o == observer)
                return true;
        }
        return false;
    }

    public interface MortalObserver extends AppEvent.Observer, LifecycleOwner {
    }
}
