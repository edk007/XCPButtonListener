package com.edtest.xcpbuttonlistener;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.samsung.android.knox.custom.CustomDeviceManager;
import com.samsung.android.knox.custom.SystemManager;
import com.samsung.android.knox.kpcc.KPCCManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class XCPButtonService extends Service {
    public static final String TAG = "XCP_BUTTON_SERVICE";
    public static final String TAG2 = "XCP_BUTTON_SERVICE: ";
    private static final String CHANNEL_ID = "RUNNING";
    public static final int JOB_ID = 0x151515;
    public static final boolean USE_KNOX = false;

    Context c;

    private CustomDeviceManager cdm;
    private SystemManager kcsm;

    private String mCameraId;
    private CameraManager mCameraManager;

    //variables to setup timing for the long press
    Runnable longPressRunnable;
    private Handler longPressHandler;
    int long_press_time = 5000; //milliseconds - 5 seconds = 5,000 milliseconds

    //variables for the task executed with a long press
    ScheduledThreadPoolExecutor exec;
    Runnable strobeRunnable;
    ScheduledFuture strobeTimerTask;
    int strobe_interval = 100;  //milliseconds
    boolean strobeState = false;

    @Override
    public void onCreate() {
        super.onCreate();

        c = this;

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        longPressHandler = new Handler(Looper.getMainLooper());

        Notification n = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("RUNNING")
                .setContentText("Service Is Running")
                .build();

        startForeground(JOB_ID,n);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.w(TAG, TAG2 + "ON_START_COMMAND");

        if (USE_KNOX) {

            cdm = CustomDeviceManager.getInstance();
            String permission = "com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM";
            if (cdm.checkEnterprisePermission(permission)) {
                Log.w(TAG, TAG2 + "KNOX_CUSTOM_SETTING_PERMISSION_GRANTED");
            } else {
                Log.w(TAG, TAG2 + "KNOX_CUSTOM_SETTING_PERMISSION_DENIED");
            }

            kcsm = cdm.getSystemManager();
            kcsm.setHardKeyIntentState(CustomDeviceManager.ON, KPCCManager.KEYCODE_PTT, (CustomDeviceManager.KEY_ACTION_DOWN | CustomDeviceManager.KEY_ACTION_UP), CustomDeviceManager.ON);
            kcsm.setHardKeyIntentState(CustomDeviceManager.ON, KPCCManager.KEYCODE_EMERGENCY, (CustomDeviceManager.KEY_ACTION_DOWN | CustomDeviceManager.KEY_ACTION_UP), CustomDeviceManager.ON);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(cdm.ACTION_HARD_KEY_REPORT);
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            XCPButtonReceiver xcpButtonReceiver = new XCPButtonReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.edtest.xcpbuttonlistener.intent.action.PTT_PRESS");
            intentFilter.addAction("com.edtest.xcpbuttonlistener.intent.action.PTT_RELEASE");
            registerReceiver(xcpButtonReceiver,intentFilter);
        }

        exec = new ScheduledThreadPoolExecutor(1);

        longPressRunnable = new Runnable() {
            @Override
            public void run() {
                //should start something after the scan interval
                Log.w(TAG, TAG2 + "LONG_PRESS_RUNNABLE");

                strobeTimerTask = exec.scheduleAtFixedRate(strobeRunnable,0,strobe_interval, TimeUnit.MILLISECONDS);
            }
        };

        strobeRunnable = new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, TAG2 + "STROBE_STATE:" + strobeState);
                try {
                    mCameraManager.setTorchMode(mCameraId,strobeState);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                strobeState = !strobeState;
            }
        };

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int keyPressed = intent.getIntExtra(cdm.EXTRA_KEY_CODE,0);
            int upDown = intent.getIntExtra(cdm.EXTRA_REPORT_TYPE,0);

            //XCP BUTTON / PTT BUTTON
            if (keyPressed == KPCCManager.KEYCODE_PTT && upDown == cdm.KEY_ACTION_DOWN) {
                Log.w(TAG, TAG2 + "PTT_DOWN");
                //start the timer for long press
                longPressHandler.postDelayed(longPressRunnable, long_press_time);
            }

            if (keyPressed == KPCCManager.KEYCODE_PTT && upDown == cdm.KEY_ACTION_UP) {
                Log.w(TAG, TAG2 + "PTT_UP");
                //if button was lifted before the long press timeout then we have a short press action to complete
                if (longPressHandler.hasCallbacks(longPressRunnable)) {
                    //we have a short press
                    Log.w(TAG, TAG2 + "SHORT_PRESS_END");
                    //remove the handler callback
                    longPressHandler.removeCallbacks(longPressRunnable);
                    //execute any short press command here:
                } else {
                    Log.w(TAG, TAG2 + "LONG_PRESS_END");
                    //cancel anything started in the long press
                    strobeTimerTask.cancel(true);
                    //make sure the flash light is off
                    turnOffLight();
                }
            }

            //TOP BUTTON
            if (keyPressed == KPCCManager.KEYCODE_EMERGENCY && upDown == cdm.KEY_ACTION_DOWN) {
                Log.w(TAG, TAG2 + "TOP_DOWN");
            }
            if (keyPressed == KPCCManager.KEYCODE_EMERGENCY && upDown == cdm.KEY_ACTION_UP) {
                Log.w(TAG, TAG2 + "TOP_UP");
            }
        }
    };

    private void turnOffLight() {
        try {
            mCameraManager.setTorchMode(mCameraId,false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
