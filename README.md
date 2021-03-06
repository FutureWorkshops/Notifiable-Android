# Notifiable-Android

**Notifiable-Android** is a library that allows you to easily integrate with
[Notifiable-Rails](https://github.com/FutureWorkshops/Notifiable-Rails).

It handles device token registration and takes care of retrying failed requests and avoiding duplicate registrations.

Registering existing token for different user will result in token being reassigned.

## Setup

1. Update your project's `build.gradle` file to include Jitpack

```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```


2. Update your app `build.gradle` file and add the Notifiable dependency
```
 implementation 'com.github.FutureWorkshops:Notifiable-Android:1.3.5@aar'
```

PS: replace `1.3.5` with the latest version available

* Don't forget the **@aar**!*

 3. Add ProGuard rule

Add the following rule to your exising ProGuard setup

```
    -keep class com.futureworkshops.notifiable.model.** { *; }
```

### Transitive dependencies

The Notifiable SDK relies on other 3rd party libraries to function **that must be available at compile time**:
-  `com.android.support:support-annotations:27.0.0`
-  `joda-time:joda-time2.10.1`
- `com.squareup.retrofit2:retrofit:2.3.0`
- `com.squareup.retrofit2:converter-gson:2.3.0`
- `com.squareup.retrofit2:converter-scalars:`
- `com.squareup.okhttp3:okhttp-urlconnection:3.10.0` 
- `com.squareup.okhttp3:logging-interceptor:3.10.0`


You can automatically add all of the above dependencies by using
```
  implementation ('com.github.FutureWorkshops:Notifiable-Android:1.3.5@aar'){
        transitive true
    }
```    

#### Dependency conflict resolution

##### 1. Exclude modules from Notifiable library

You can exclude as many modules as you want:

```
 implementation ('com.github.FutureWorkshops:Notifiable-Android:1.3.4'){
     exclude group: 'com.android.support', module:'support-annotations'
     exclude group: 'joda-time', module:'joda-time'
 }
```


##### 2. Force resolution of dependency

You can resolve the conflict by specifying which dependecy version should be used by the entire app 

```
android {
    configurations.all {
        resolutionStrategy.force 'joda-time:joda-time2.10.1'
    }
}
```

### Add Notifiable manually 

You can also download the `.aar` and use it directly but in this case you 
will **not be able to manage transitive dependencies**!


 1. Download the latest `.aar` from the *Releases* tab on github.

 2. Update your project `build.gradle`
Update the project *build.gradle* file (the one from the root of the project) 

```
allprojects {
    repositories {
        flatDir {
            dirs 'src/main/libs'
        }
    }
}
```

 3. Add dependency in your app's `build.gradle`

You are now ready to add the dependency to your application

```
 implementation(name: "NotifiableAndroidSDK-${notifiableVersion}", ext: 'aar')

```

4. Add required dependencies
```
 implementation  'com.android.support:support-annotations:27.0.0'
 implementation  'joda-time:joda-time2.10.1'
 implementation  'com.squareup.retrofit2:retrofit:2.3.0'
 implementation  'com.squareup.retrofit2:converter-gson:2.3.0'
 implementation  'com.squareup.retrofit2:converter-scalars:'
 implementation  'com.squareup.okhttp3:okhttp-urlconnection:3.10.0' 
 implementation  'com.squareup.okhttp3:logging-interceptor:3.10.0'
```

5. Add ProGuard rule

Add the following rule to your exising ProGuard setup

```
    -keep class com.futureworkshops.notifiable.model.** { *; }
```

## Usage

To use the `NotifiableManager`, create a new object passing:

*  your server URL
*  application access id 
*  application secret key.

```java
 mNotifiableManager = NotifiableManager.newInstance(Constants.NOTIFIABLE_SERVER,
                Constants.NOTIFIABLE_CLIENT_ID, Constants.NOTIFIABLE_CLIENT_SECRET);
```

## Registering a device

In order to reister a device in the `Notifiable-Rails` server you need to obtain a **Google Cloud Messaging (GCM)** token first - see [Sample folder](sample-app) for an example.
 
 After you have a GCM token you can register a device :

```java
 mNotifiableManager.registerDevice(deviceName, mGcmToken, user,
    Locale.ENGLISH, NotifiableManager.GOOGLE_CLOUD_MESSAGING_PROVIDER,
    new NotifiableCallback<NotifiableDevice>() {
        @Override
        public void onSuccess(NotifiableDevice device) {
            // do something with the device
        }

        @Override
        public void onError(String error) {
            // show error
        }
    });
```

If you don't want to assign a device to a user you can use the anonymous registration method:

```java
mNotifiableManager.registerAnonymousDevice(deviceName, mGcmToken,
    Locale.ENGLISH, NotifiableManager.GOOGLE_CLOUD_MESSAGING_PROVIDER,
    new NotifiableCallback<NotifiableDevice>() {
        @Override
        public void onSuccess(NotifiableDevice device) {
            // do something with the device
        }

        @Override
        public void onError(String error) {
            // show error
        }
    });
```


## Updating the device information

Once that the device is registered, you can update the device information:

```java
mNotifiableManager.updateDeviceCustomProperties(String.valueOf(mDeviceId), deviceInfo, notifiableCallback);
```
```deviceInfo``` is a  Map<String,Object> containing properties that can be defined for a device. These properties are configured by the `Notifiable-Rails` application.


## Updating the device name

The library provides methods to update the device name for both anonymous and assigned devices 

```java
        if (TextUtils.isEmpty(mDeviceUser)) {
            mNotifiableManager.updateAnonymousDeviceName(
            String.valueOf(mDeviceId), name, callback);
        } else {
            mNotifiableManager.updateDeviceName(mDeviceUser,
             String.valueOf(mDeviceId), name, callback);
        }
```
* `callback` parameter is a `NotifiableCallback<NotifiableDevice>`


## Updating the device locale

Updating the device Locale is equally easy

```java
mNotifiableManager.updateDeviceLocale(String.valueOf(mDeviceId), locale, callback);
```
* `callback` parameter is a `NotifiableCallback<NotifiableDevice>`
* `locale` parameter is a `Locale` object


## Updating the device token

Sometimes Google will issue a new **GCM token** for a registered device if the previous has been compromised. When this happens you need to make the `Notifiable-Rails` app aware of the changes.

```java
if (TextUtils.isEmpty(mDeviceUser)) {
    mNotifiableManager.updateDeviceToken(mDeviceUser,
     String.valueOf(mDeviceId), mGcmToken, callback);
} else {
    mNotifiableManager.updateAnonymousDeviceToken(
    String.valueOf(mDeviceId), mGcmToken, callback);
}
```


## Unassigning the device from a user

If you registered a device and associated it with a user but the user no longer exists you can easily anonymise the device :

```java
    mNotifiableManager.unassignDeviceFromUser(mDeviceName, mGcmToken, callback);
```
* `callback` parameter is a `NotifiableCallback<NotifiableDevice>`



## Assigning the device to a user

Just as easily, if you have an anonymous device you can assign it to a user :

```java
 mNotifiableManager.assignDeviceToUser(userName, mDeviceName, mGcmToken, callback);
```
* `callback` parameter is a `NotifiableCallback<NotifiableDevice>`


## Unregister a device

You may wish to unregister a device. To do this you need to provide :
<ul>
	<li> the name of the user that this device was assigned to (or empty string if the device was registered anonymously)</li>
	<li> the device id returned by the server after you registered</li>
</ul>

```java
mNotifiableManager.unregisterDevice(String.valueOf(mDeviceId),mDeviceUser, 
    new NotifiableCallback<Object>() {
      @Override
     public void onSuccess(Object ret) {
        // do something
     }

     @Override
     public void onError(String error) {
         // show error
     }
});
```


## Marking a notification as received

This method can be used to let the `Notifiable-server` know that a notification was succesfully delivered to a device.

This method should be called as soon as possible after the notification was received, without waiting for any other user interaction.

```java
mNotifiableManager.markNotificationReceived(notificationId, deviceId,
    new NotifiableCallback<Object>() {
        @Override
        public void onSuccess(Object ret) {
            // notifications was marked as open
        }

        @Override
        public void onError(String error) {
           // show error
        }
    });
```


## Marking a notification as opened
When the application is launched or has received a remote notification, you can relay the fact it was opened by the user to <a href="https://github.com/FutureWorkshops/Notifiable-Rails">Notifiable-Rails</a>.

```java
mNotifiableManager.markNotificationOpened(notificationId, deviceId,
    new NotifiableCallback<Object>() {
        @Override
        public void onSuccess(Object ret) {
            // notifications was marked as open
        }

        @Override
        public void onError(String error) {
           // show error
        }
    });
```



The notification details are received in the `Intent` object obtained from FCM.
The library has a method that helps you convert data from the intent to a `NotifiableMessage` (which will have the notification id, the notification message and all the other information sent by the server).

```java
Utils.createNotificationFromMap(data);
```

## Server token invalidation

The **Notifiable** server can automatically handle multiple registrations from the same device:

- Client (iOS/Android App) gets a token from APNS/GCM
- Sends to Notifiable
- Notifiable checks if the token exists (not - not the user)
- If token exists we return the Notifiable ID for that token.
- If token does not exist we create a new Notifiable ID
- Client can then update tags / custom properties associated with that Notifiable ID
- Notifiable sends notifications. If GCM/APNS says the token was invalid we remove the token.

## LICENSE

[Apache License Version 2.0](LICENSE)
