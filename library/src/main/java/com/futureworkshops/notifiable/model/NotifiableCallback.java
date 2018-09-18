/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;

/**
 * Created by stelian on 22/03/2016.
 */
public interface NotifiableCallback<S> {

    void onSuccess(S ret);

    void onError(String error);
}
