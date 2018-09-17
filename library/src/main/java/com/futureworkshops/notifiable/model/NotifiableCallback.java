/*
 * Copyright (c) 2018 Future Workshops. All rights reserved.
 */

package com.futureworkshops.notifiable.model;

import android.support.annotation.NonNull;

/**
 * Created by stelian on 22/03/2016.
 */
public interface NotifiableCallback<S> {

    void onSuccess(@NonNull S ret);

    void onError(@NonNull String error);
}
