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
import edu.upm.findme.model.UserDetails;

public class MqttTalker implements PersistentSessionMqttClient.EventHandler {
    final static String LOG_TAG = "MQTT_ERROR";
    final static String TOPIC_MESSAGE = "messages/";
    final static String TOPIC_STEPS = "steps/";
    final static String TOPIC_LOCATION = "locations/";
    final static String TOPIC_STATUS = "status/";
    final static String MESSAGE_FIELD_SEPARATOR = "_";

    final PersistentSessionMqttClient client;
    final AppEvent.Observer observer;
    final int userId;

    int numberOfUnreadMessages = 0;
    boolean hasBeenSubscribedToMessages = false;
    boolean hasBeenStarted = false;
    boolean connected = false;
    UserDetails.Status lastPublishedStatus = UserDetails.Status.OFFLINE;
    List<Message> messages = new ArrayList<>();
    Map<Integer, Integer> steps = new HashMap<>();
    Map<Integer, Location> locations = new HashMap<>();
    Map<Integer, UserDetails.Status> statuses = new HashMap<>();

    public MqttTalker(Context appContext, AppEvent.Observer observer, int userId) {
        client = new PersistentSessionMqttClient(appContext, this, userId);
        this.observer = observer;
        this.userId = userId;
    }

    public boolean isConnected() {
        return connected;
    }

    public void start(int groupId) {
        if (!hasBeenStarted) {
            PersistentSessionMqttClient.LastWillOptions lastWill = new PersistentSessionMqttClient.LastWillOptions();
            lastWill.topic = TOPIC_STATUS + userId;
            lastWill.payload = UserDetails.Status.OFFLINE.toString();
            lastWill.qos = 1;
            lastWill.retain = true;
            client.connect(String.valueOf(groupId), lastWill);
            hasBeenStarted = true;
        }
    }

    public void stop() {
        numberOfUnreadMessages = 0;
        messages.clear();
        statuses.clear();
        locations.clear();
        statuses.clear();
        client.unSubscribe("#");
        client.publishMessage(TOPIC_STATUS + userId, "", 1, true);
        client.publishMessage(TOPIC_STEPS + userId, "", 1, true);
        client.publishMessage(TOPIC_LOCATION + userId, "", 1, false);
        client.disconnect();
        hasBeenStarted = false;
        hasBeenSubscribedToMessages = false;
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

    public Map<Integer, UserDetails.Status> getStatuses() {
        return statuses;
    }

    public UserDetails.Status getLastPublishedStatus() {
        return lastPublishedStatus;
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
            } else if (topic.startsWith(TOPIC_STATUS)) {
                int id = getUserIdByTopic(topic);
                UserDetails.Status status = UserDetails.getStatusFromString(payload);

                statuses.put(id, status);
                observer.onGlobalEvent(AppEvent.Type.STATUS_DATABASE_CHANGED);

                if (status != UserDetails.Status.LIVE)
                    if (locations.containsKey(id)) {
                        locations.remove(id);
                        observer.onGlobalEvent(AppEvent.Type.LOCATION_DATABASE_CHANGED);
                    }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error during message parsing: " + e.toString());
        }
    }

    public void setLocationUpdates(boolean enabled) {
        if (enabled)
            client.subscribe(TOPIC_LOCATION + "#", 1);
        else {
            // unsubscribe not working
            client.unSubscribe(TOPIC_LOCATION + "#");
            locations.clear();
        }
    }

    private int getUserIdByTopic(String topic) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        String idAsString = topic.split("/")[1];
        return Integer.parseInt(idAsString);
    }

    private Location parseLocationMessage(String message) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        Location location = new Location("");
        String[] fields = message.split(MESSAGE_FIELD_SEPARATOR);

        location.setLatitude(Double.parseDouble(fields[0]));
        location.setLongitude(Double.parseDouble(fields[1]));
        return location;
    }

    public void publishUserStatus(UserDetails.Status status) {
        lastPublishedStatus = status;
        client.publishMessage(TOPIC_STATUS + userId, status.toString(), 1, true);
    }

    @Override
    public void onError(String errorDescription) {
        Log.d(LOG_TAG, errorDescription);
    }

    @Override
    public void onConnectionStateChange(boolean isConnected, String optionalInfo) {
        if (optionalInfo != null)
            Log.d(LOG_TAG, optionalInfo);

        if (isConnected) {
            publishUserStatus(UserDetails.Status.ONLINE);

            if (!hasBeenSubscribedToMessages) {
                client.subscribe(TOPIC_MESSAGE + "#", 2);
                client.subscribe(TOPIC_STEPS + "#", 0);
                client.subscribe(TOPIC_STATUS + "#", 0);
                hasBeenSubscribedToMessages = false;
            }
        }

        connected = isConnected;
        observer.onGlobalEvent(AppEvent.Type.MQTT_CONNECTION_CHANGE);
    }
}
