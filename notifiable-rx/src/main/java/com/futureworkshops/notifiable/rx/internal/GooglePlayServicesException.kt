/*
 * Copyright ©  2018 - 2019 FutureWorkshops. All rights reserved.
 */

package com.futureworkshops.notifiable.rx.internal


/**
 * Custom exception class used to catch recoverable and non-recoverable Google Play services errors.
 *
 *     If any error is thrown, you can use GoogleApiAvailability to show an error dialog:
 *
 * ```
 * val apiAvailability = GoogleApiAvailability.getInstance()
 * if (apiAvailability.isUserResolvableError(exceptionCode)) {
apiAvailability.getErrorDialog(activity,exceptionCode, PLAY_SERVICES_RESOLUTION_REQUEST)
.show()
}
 * ```
 */
class GooglePlayServicesException(val exceptionCode: Int) :
    RuntimeException("We couldn't find Google Play Services on the device $exceptionCode")
