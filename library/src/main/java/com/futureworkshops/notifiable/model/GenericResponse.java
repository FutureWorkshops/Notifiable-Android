/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;


public class GenericResponse {

    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
