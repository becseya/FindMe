package edu.upm.findme.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

public class UserInfoManager {

    private static final String PREF_NAME = "userinfo_manager";
    private static final String KEY_USER_ID = "user_id";

    private static final int ID_NULL = 0;

    private final Context context;
    private boolean hasBeenLoaded = false;
    private int userId = ID_NULL;

    public UserInfoManager(Context activity) {
        this.context = activity;
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void load() {
        SharedPreferences pref = getPreferences();
        userId = pref.getInt(KEY_USER_ID, ID_NULL);
    }

    private void checkAndLoad() {
        if (!hasBeenLoaded) {
            load();
            hasBeenLoaded = true;
        }
    }

    private void save() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        checkAndLoad();
        return userId;
    }

    public void setUserId(int id) {
        userId = id;
        save();
    }

    public boolean isUserIdSet() {
        return (getUserId() != ID_NULL);
    }
}
