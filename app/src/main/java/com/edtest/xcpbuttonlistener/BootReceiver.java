package com.edtest.xcpbuttonlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.knox.custom.CustomDeviceManager;

import java.util.Set;

public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = "XCP_BUTTON_SERVICE";
    public static final String TAG2 = "BOOT_RECEIVER: ";
    public static final boolean USE_KNOX = false;

    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            c = context;
            Log.w(TAG, TAG2 + "onReceive");

            //start the service
            Intent i = new Intent(c, XCPButtonService.class);
            context.startForegroundService(i);
        }
    }
}
