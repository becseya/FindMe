package edu.upm.findme.utility;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.upm.findme.AppEvent;
import edu.upm.findme.model.Message;

public class MqttTalker implements PersistentSessionMqttClient.EventHandler {
    final static String LOG_TAG = "MQTT_ERROR";
    final static String TOPIC_MESSAGE = "messages/";

    final PersistentSessionMqttClient client;
    final AppEvent.Observer observer;
    final int userId;

    int numberOfUnreadMessages = 0;
    boolean hasBeenSubscribedToMessages = false;
    boolean hasBeenStarted = false;
    boolean connected = false;
    List<Message> messages = new ArrayList<>();

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

    public void sendMessage(Message msg) {
        client.publishMessage(TOPIC_MESSAGE + msg.getSenderId(), msg.getContent(), 2, false);
    }

    @Override
    public void onMessage(String topic, String payload) {
        try {
            // Always take care to increase numberOfUnreadMessages after possible exception, but before sending the event
            if (topic.startsWith(TOPIC_MESSAGE)) {
                messages.add(new Message(getUserIdByTopic(topic), payload));
                numberOfUnreadMessages++;
                observer.onGlobalEvent(AppEvent.Type.MEW_MESSAGE);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error during message parsing");
        }
    }

    private int getUserIdByTopic(String topic) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        String idAsString = topic.split("/")[1];
        return Integer.parseInt(idAsString);
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
            hasBeenSubscribedToMessages = false;
        }

        connected = isConnected;
        observer.onGlobalEvent(AppEvent.Type.MQTT_CONNECTION_CHANGE);
    }
}
