package edu.upm.findme;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;
import java.util.Map;

import edu.upm.findme.model.User;
import edu.upm.findme.model.UserDetails;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.Locator;
import edu.upm.findme.utility.MqttTalker;
import edu.upm.findme.utility.StepSensor;
import edu.upm.findme.utility.UserInfoManager;

public class App extends Application implements AppEvent.Observer, StepSensor.SensorInterface, Locator.LocationHandler {

    public UserInfoManager userInfo;
    public MqttTalker mqtt;
    public StepSensor stepSensor;
    public Locator locator;
    public List<User> users;
    public int singleUserOnMap;

    boolean hasBeenInitialized;
    MortalObserver currentObserver = null;

    public App init() {
        if (!hasBeenInitialized) {
            userInfo = new UserInfoManager(this);
            mqtt = new MqttTalker(this, this, userInfo.getUserId());
            stepSensor = new StepSensor(this, this);
            locator = new Locator(this, this);
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

        if (e == AppEvent.Type.STATUS_DATABASE_CHANGED)
            lookForNewUsers();
        else if (e == AppEvent.Type.USER_LEFT_GROUP)
            refreshUserList();
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

    @Override
    public void onNewLocation(Location location) {
        UserDetails.Status newStatus = (locator.isRunning() ? UserDetails.Status.LIVE : UserDetails.Status.ONLINE);

        if (locator.isRunning())
            mqtt.publishLocation(location);

        if (newStatus != mqtt.getLastPublishedStatus())
            mqtt.publishUserStatus(newStatus);
    }

    void lookForNewUsers() {
        for (Map.Entry<Integer, UserDetails.Status> entry : mqtt.getStatuses().entrySet()) {
            if (User.getById(users, entry.getKey()) == null) {
                refreshUserList();
                return;
            }
        }
    }

    void refreshUserList(){
               ApiClient api = new ApiClient((errorDescription) -> {/*ignore failure*/});

                api.listUsers(userInfo.getGroupId(), (fetchedUsers) -> {
                    this.users = fetchedUsers;
                    onGlobalEvent(AppEvent.Type.GROUP_USERS_CHANGED);
                });
    }

    public interface MortalObserver extends AppEvent.Observer, LifecycleOwner {
    }
}
