# device-id-test

A relatively useless test app which can be used to check the behavior of dual-SIM Android devices.
The app displays the current default IMEI number (as returned by TelephonyManager.getDeviceId()), so
one can run the app multiple times while toggling the active SIM slot in the settings app in between
different runs to see if that also changes the default IMEI.