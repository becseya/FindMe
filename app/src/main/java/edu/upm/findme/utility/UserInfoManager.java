package edu.upm.findme.utility;

import android.content.Context;
import android.content.SharedPreferences;

import edu.upm.findme.model.Group;

public class UserInfoManager {

    private static final String PREF_NAME = "userinfo_manager";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_TOTAL_STEPS = "total_steps";

    private static final int ID_NULL = 0;

    private final Context context;
    private boolean hasBeenLoaded = false;
    private int userId;
    private int groupId;
    private String groupName;
    private int totalSteps;

    public UserInfoManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void load() {
        SharedPreferences pref = getPreferences();
        userId = pref.getInt(KEY_USER_ID, ID_NULL);
        groupId = pref.getInt(KEY_GROUP_ID, ID_NULL);
        groupName = pref.getString(KEY_GROUP_NAME, "NULL");
        totalSteps = pref.getInt(KEY_TOTAL_STEPS, 0);
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
        editor.putInt(KEY_GROUP_ID, groupId);
        editor.putString(KEY_GROUP_NAME, groupName);
        editor.putInt(KEY_TOTAL_STEPS, totalSteps);
        editor.apply();
    }

    public int getUserId() {
        checkAndLoad();
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        save();
    }

    public int getGroupId() {
        checkAndLoad();
        return groupId;
    }

    public String getGroupName() {
        checkAndLoad();
        return groupName;
    }

    public void setGroupInfo(Group group) {
        this.groupId = group.getId();
        this.groupName = group.getName();
        save();
    }

    public int getTotalSteps() {
        checkAndLoad();
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
        save();
    }

    public boolean isUserIdSet() {
        return (getUserId() != ID_NULL);
    }

    public boolean isGroupIdSet() {
        return (getGroupId() != ID_NULL);
    }
}
