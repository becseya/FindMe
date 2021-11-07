package edu.upm.findme.utility;

import android.Manifest;
import android.view.Menu;
import android.widget.Switch;

import androidx.fragment.app.FragmentActivity;

import edu.upm.findme.R;

public class MenuManager {
    FragmentActivity activity;
    SwitchStateChangeListener listener;
    PermissionRequester permissionRequester;
    Switch switchPosition;

    public MenuManager(FragmentActivity activity, SwitchStateChangeListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.permissionRequester = new PermissionRequester(activity);
    }

    public void onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.actionbar_tools, menu);

        switchPosition = menu.findItem(R.id.actionbar_tools_switch).getActionView().findViewById(R.id.switchPosition);
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

    public interface SwitchStateChangeListener {
        void onSwitchChange(boolean checked);
    }
}