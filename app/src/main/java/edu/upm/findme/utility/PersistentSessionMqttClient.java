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

public class PersistentSessionMqttClient implements MqttCallbackExtended {
    final static String SERVER_URI = "tcp://broker.hivemq.com";
    final static String TOPIC_BASE = "findme.upm.edu/";
    final static String CLIENT_ID_BASE = TOPIC_BASE + "user";

    final String clientId;
    final MqttAndroidClient client;
    final EventHandler handler;

    public PersistentSessionMqttClient(Context appContext, EventHandler handler, int id) {
        this.clientId = CLIENT_ID_BASE + id;
        this.client = new MqttAndroidClient(appContext, SERVER_URI, clientId);
        this.handler = handler;
    }

    public void connect() {
        client.setCallback(this);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

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

    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(TOPIC_BASE + topic, qos, null, new IMqttActionListener() {
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
            client.unsubscribe(topic);
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
            client.publish(TOPIC_BASE + topic, message);
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
        handler.onMessage(topic.substring(TOPIC_BASE.length()), new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public interface EventHandler {
        void onMessage(String topic, String payload);

        void onError(String errorDescription);

        void onConnectionStateChange(boolean isConnected, String optionalInfo);
    }
}
