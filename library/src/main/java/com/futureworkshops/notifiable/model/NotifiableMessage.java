/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by stelian on 01/04/2016.
 */
public class NotifiableMessage implements Serializable {

    private int notificationId;
    private String title;
    private String message;
    private Map<String, String> deviceProperties;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDeviceProperties() {
        return deviceProperties;
    }

    public void setDeviceProperties(Map<String, String> deviceProperties) {
        this.deviceProperties = deviceProperties;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
