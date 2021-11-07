package edu.upm.findme;

public class AppEvent {
    public enum Type {
        MEW_MESSAGE,
        STEP_SCORES_CHANGED,
        STEP_TAKEN_BY_USER,
        MQTT_CONNECTION_CHANGE
    }

    public interface Observer {
        void onGlobalEvent(Type e);
    }
}
