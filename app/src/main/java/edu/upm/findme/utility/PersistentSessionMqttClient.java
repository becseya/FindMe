package edu.upm.findme.utility;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class PersistentSessionMqttClient implements MqttCallbackExtended {
    final static String SERVER_URI = "tcp://broker.hivemq.com";
    final static String TOPIC_ROOT = "findme.upm.edu/";
    final static String CLIENT_ID_BASE = TOPIC_ROOT + "user";

    final String clientId;
    final MqttAndroidClient client;
    final EventHandler handler;
    String topicBase;

    public PersistentSessionMqttClient(Context appContext, EventHandler handler, int id) {
        this.clientId = CLIENT_ID_BASE + id;
        this.client = new MqttAndroidClient(appContext, SERVER_URI, clientId);
        this.handler = handler;
    }

    public void connect(String topicBase) {
        connect(topicBase, null);
    }

    public void connect(String topicBase, LastWillOptions lastWill) {
        this.topicBase = topicBase;

        client.setCallback(this);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        if (lastWill != null) {
            mqttConnectOptions.setKeepAliveInterval(5);
            mqttConnectOptions.setWill(getTopicBase() + lastWill.topic, lastWill.payload.getBytes(StandardCharsets.UTF_8), lastWill.qos, lastWill.retain);
        }

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    handler.onError("Failed to connect" + e.toString());
                }
            });
        } catch (Exception e) {
            handler.onError("Failed to connect: " + e.toString());
        }
    }

    public boolean disconnect() {
        try {
            client.disconnect();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(getTopicBase() + topic, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    handler.onError("Failed to subscribe: " + e.toString());
                }
            });
        } catch (Exception e) {
            handler.onError("Failed to subscribe: " + e.toString());
        }
    }

    public void unSubscribe(String topic) {
        try {
            client.unsubscribe(getTopicBase() + topic);
        } catch (Exception e) {
            handler.onError("Failed to unsubscribe: " + e.toString());
        }
    }

    public void publishMessage(String topic, String payload, int qos, boolean retain) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(qos);
            message.setRetained(retain);
            client.publish(getTopicBase() + topic, message);
        } catch (Exception e) {
            handler.onError("Failed to publish: " + e.toString());
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        handler.onConnectionStateChange(true, null);
    }

    @Override
    public void connectionLost(Throwable cause) {
        String causeStr = (cause != null) ? cause.toString() : null;
        handler.onConnectionStateChange(false, causeStr);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // unsubscribe doesn't seems to work, this is a dirty workaround
        if (topic.startsWith(getTopicBase()))
            handler.onMessage(topic.substring(getTopicBase().length()), new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    private String getTopicBase() {
        if (topicBase.isEmpty())
            return TOPIC_ROOT;
        else
            return TOPIC_ROOT + topicBase + "/";
    }

    public interface EventHandler {
        void onMessage(String topic, String payload);

        void onError(String errorDescription);

        void onConnectionStateChange(boolean isConnected, String optionalInfo);
    }

    public static class LastWillOptions {
        public String topic;
        public String payload;
        public int qos;
        public boolean retain;
    }
}
