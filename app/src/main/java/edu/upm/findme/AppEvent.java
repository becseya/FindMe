package edu.upm.findme;

public class AppEvent {
    public enum Type {
        MEW_MESSAGE,
        STEP_SCORES_CHANGED,
        STEP_TAKEN_BY_USER,
        LOCATION_DATABASE_CHANGED,
        STATUS_DATABASE_CHANGED,
        USER_LEFT_GROUP,
        GROUP_USERS_CHANGED,
        MQTT_CONNECTION_CHANGE
    }

    public interface Observer {
        void onGlobalEvent(Type e);
    }
}
