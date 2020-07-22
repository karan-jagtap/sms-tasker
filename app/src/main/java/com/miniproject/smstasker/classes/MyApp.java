package com.miniproject.smstasker.classes;

import android.app.Application;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.miniproject.smstasker.helper.PhoneStateReceiver;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SMSTasker", "onCreate() - app");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i("SMSTasker", "onTerminate() - app");
    }
}
