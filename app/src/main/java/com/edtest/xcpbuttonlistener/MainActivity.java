package com.edtest.xcpbuttonlistener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.samsung.android.knox.EnterpriseDeviceManager;
import com.samsung.android.knox.restriction.RestrictionPolicy;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "XCP_BUTTON_SERVICE";
    public static final String TAG2 = "MAIN_ACTIVITY: ";
    private static final String CHANNEL_ID = "RUNNING";
    public static final int JOB_ID = 0x151515;

    //KNOX
    private static final int DEVICE_ADMIN_ADD_RESULT_ENABLE = 1;
    private ComponentName mDeviceAdmin;
    private DevicePolicyManager mDPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SAMSUNG KNOX
        if (Build.BRAND.equals("samsung")) {
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDeviceAdmin = new ComponentName(MainActivity.this, AdminReceiver.class);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        boolean knox = true;
        boolean da = true;
        if (Build.BRAND.equals("samsung")) {
            knox = checkKnox();
            da = mDPM.isAdminActive(mDeviceAdmin);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || !da || !knox) {
            //if we don't have permission - need to get it granted
            Intent intent = new Intent(getApplicationContext(), PermissionsActivity.class);
            startActivity(intent);
        } else {
            createNotificationChannel();

            Intent s = new Intent(this,XCPButtonService.class);
            startForegroundService(s);

            this.finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RUNNING";
            String description = "Service is running";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.w(TAG, TAG2 + "CREATE_NOTIFICATION_CHANNEL_FAILED");
            }
        }
    }

    private boolean checkKnox() {
        EnterpriseDeviceManager enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(this);
        RestrictionPolicy restrictionPolicy = enterpriseDeviceManager.getRestrictionPolicy();
        boolean isCameraEnabled = restrictionPolicy.isCameraEnabled(false);
        try {
            // this is a fake test - if it throws an exception we do not have DA or we do not have an active license
            boolean result = restrictionPolicy.setCameraState(!isCameraEnabled);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    } //checkKnox
}