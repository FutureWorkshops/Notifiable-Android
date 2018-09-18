/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by stelian on 22/03/2016.
 */
public class NotifiableDevice {

    private int id;
    private String name;
    private String user;
    private String token;
    private Locale locale;
    private HashMap<String,Object> customProperties;


    @SerializedName("created_at")
    private String createdAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public HashMap<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(HashMap<String, Object> customProperties) {
        this.customProperties = customProperties;
    }
}
