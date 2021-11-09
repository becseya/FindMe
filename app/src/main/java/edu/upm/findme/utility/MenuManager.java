package edu.upm.findme.utility;

import android.Manifest;
import android.graphics.drawable.GradientDrawable;
import android.view.Menu;
import android.view.View;
import android.widget.Switch;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;

public class MenuManager implements AppEvent.Observer {
    App app;
    FragmentActivity activity;
    SwitchStateChangeListener listener;
    PermissionRequester permissionRequester;
    Switch switchPosition;
    GradientDrawable statusCircle;

    public MenuManager(FragmentActivity activity, App app) {
        this.app = app;
        this.activity = activity;
        this.listener = app.locator;
        this.permissionRequester = new PermissionRequester(activity);
    }

    public void onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.actionbar_tools, menu);

        View root = menu.findItem(R.id.actionbar_tools_switch).getActionView();
        statusCircle = (GradientDrawable) root.findViewById(R.id.connectionStatus).getBackground();
        switchPosition = root.findViewById(R.id.switchPosition);

        activity.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_RESUME)
                updateUI();
        });

        switchPosition.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (!checked)
                listener.onSwitchChange(false);
            else {
                compoundButton.setChecked(false);
                permissionRequester.askAndDisplayToastIfDenied(Manifest.permission.ACCESS_FINE_LOCATION, () -> {
                    compoundButton.setChecked(true);
                    listener.onSwitchChange(true);
                });
            }
        });
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (e == AppEvent.Type.MQTT_CONNECTION_CHANGE)
            updateUI();
    }

    private void updateUI() {
        int circleColor = activity.getResources().getColor(app.mqtt.isConnected() ? R.color.online_green : R.color.offline_grey);

        statusCircle.setColor(circleColor);
        switchPosition.setChecked(app.locator.isRunning());

        if (!app.mqtt.isConnected() && app.locator.isRunning())
            switchPosition.setChecked(false);
        switchPosition.setEnabled(app.mqtt.isConnected());
    }

    public interface SwitchStateChangeListener {
        void onSwitchChange(boolean checked);
    }
}