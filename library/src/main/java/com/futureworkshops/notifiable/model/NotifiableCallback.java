/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;

public interface NotifiableCallback<S> {

    void onSuccess(S ret);

    void onError(String error);
}
