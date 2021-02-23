package com.edtest.xcpbuttonlistener;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.samsung.android.knox.custom.CustomDeviceManager;
import com.samsung.android.knox.custom.SystemManager;
import com.samsung.android.knox.kpcc.KPCCManager;

public class XCPButtonReceiver  extends BroadcastReceiver {
    public static final String TAG = "XCP_BUTTON_SERVICE";
    public static final String TAG2 = "XCP_BUTTON_RECEIVER: ";

    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, TAG2 + "ON_RECEIVE");
        String mCameraId = null;
        CameraManager mCameraManager;
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if ("com.edtest.xcpbuttonlistener.intent.action.PTT_PRESS".equals(intent.getAction())) {
            // XCover key pressed
            Log.w(TAG, TAG2 + "XCP_KEY_PRESSED");
            try {
                mCameraManager.setTorchMode(mCameraId,true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if ("com.edtest.xcpbuttonlistener.intent.action.PTT_RELEASE".equals(intent.getAction())) {
            //XCover Key Released
            Log.w(TAG, TAG2 + "XCP_KEY_RELEASED");
            try {
                mCameraManager.setTorchMode(mCameraId,false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}