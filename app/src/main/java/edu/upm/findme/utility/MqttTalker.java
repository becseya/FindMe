package edu.upm.findme.utility;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upm.findme.AppEvent;
import edu.upm.findme.model.Message;

public class MqttTalker implements PersistentSessionMqttClient.EventHandler {
    final static String LOG_TAG = "MQTT_ERROR";
    final static String TOPIC_MESSAGE = "messages/";
    final static String TOPIC_STEPS = "steps/";
    final static String TOPIC_LOCATION = "locations/";
    final static String MESSAGE_FIELD_SEPARATOR = "+";

    final PersistentSessionMqttClient client;
    final AppEvent.Observer observer;
    final int userId;

    int numberOfUnreadMessages = 0;
    boolean hasBeenSubscribedToMessages = false;
    boolean hasBeenStarted = false;
    boolean connected = false;
    List<Message> messages = new ArrayList<>();
    Map<Integer, Integer> steps = new HashMap<>();
    Map<Integer, Location> locations = new HashMap<>();

    public MqttTalker(Context appContext, AppEvent.Observer observer, int userId) {
        client = new PersistentSessionMqttClient(appContext, this, userId);
        this.observer = observer;
        this.userId = userId;
    }

    public boolean isConnected() {
        return connected;
    }

    public void start() {
        if (!hasBeenStarted) {
            client.connect();
            hasBeenStarted = true;
        }
    }

    public int getNumberOfUnreadMessages() {
        return numberOfUnreadMessages;
    }

    public List<Message> getMessages() {
        numberOfUnreadMessages = 0;
        return messages;
    }

    public Map<Integer, Integer> getSteps() {
        return steps;
    }

    public Map<Integer, Location> getLocations() {
        return locations;
    }

    public void sendMessage(Message msg) {
        client.publishMessage(TOPIC_MESSAGE + msg.getSenderId(), msg.getContent(), 2, false);
    }

    public void publishStepsTaken(int steps) {
        client.publishMessage(TOPIC_STEPS + userId, String.valueOf(steps), 1, true);
    }

    public void publishLocation(Location location) {
        String latLonString = location.getLatitude() + MESSAGE_FIELD_SEPARATOR + location.getLongitude();
        client.publishMessage(TOPIC_LOCATION + userId, latLonString, 1, false);
    }

    @Override
    public void onMessage(String topic, String payload) {
        try {
            if (topic.startsWith(TOPIC_MESSAGE)) {
                // Always take care to increase numberOfUnreadMessages after possible exception, but before sending the event
                messages.add(new Message(getUserIdByTopic(topic), payload));
                numberOfUnreadMessages++;
                observer.onGlobalEvent(AppEvent.Type.MEW_MESSAGE);
            } else if (topic.startsWith(TOPIC_STEPS)) {
                int id = getUserIdByTopic(topic);
                int stepsTaken = Integer.parseInt(payload);

                steps.put(id, stepsTaken);
                observer.onGlobalEvent(AppEvent.Type.STEP_SCORES_CHANGED);
            } else if (topic.startsWith(TOPIC_LOCATION)) {
                int id = getUserIdByTopic(topic);
                Location location = parseLocationMessage(payload);

                locations.put(id, location);
                observer.onGlobalEvent(AppEvent.Type.LOCATION_DATABASE_CHANGED);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error during message parsing");
        }
    }

    private int getUserIdByTopic(String topic) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        String idAsString = topic.split("/")[1];
        return Integer.parseInt(idAsString);
    }

    private Location parseLocationMessage(String message) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        Location location = new Location("");
        String[] fields = message.split(MESSAGE_FIELD_SEPARATOR);

        location.setLatitude(Integer.parseInt(fields[0]));
        location.setLongitude(Integer.parseInt(fields[1]));
        return location;
    }

    @Override
    public void onError(String errorDescription) {
        Log.d(LOG_TAG, errorDescription);
    }

    @Override
    public void onConnectionStateChange(boolean isConnected, String optionalInfo) {
        if (optionalInfo != null)
            Log.d(LOG_TAG, optionalInfo);

        if (isConnected && !hasBeenSubscribedToMessages) {
            client.subscribe(TOPIC_MESSAGE + "#", 2);
            client.subscribe(TOPIC_STEPS + "#", 0);
            hasBeenSubscribedToMessages = false;
        }

        connected = isConnected;
        observer.onGlobalEvent(AppEvent.Type.MQTT_CONNECTION_CHANGE);
    }
}
