package com.edtest.xcpbuttonlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager;
import com.samsung.android.knox.license.EnterpriseLicenseManager;

public class KnoxLicenseReceiver extends BroadcastReceiver {
    public static final String TAG = "XCP_BUTTON_SERVICE";
    public static final String TAG2 = "KNOX_LICENSE_RECEIVER: ";

    private int DEFAULT_ERROR_CODE = -1;

    private void showToast(Context context, int msg_res) {
        Toast.makeText(context, context.getResources().getString(msg_res), Toast.LENGTH_SHORT).show();
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int msg_res = -1;

        if (intent == null) {
            // No intent action is available
            showToast(context, R.string.no_intent);
            return;
        } else {
            String action = intent.getAction();
            if (action == null) {
                // No intent action is available
                showToast(context, R.string.no_intent_action);
                return;
            }  else if (action.equals(KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
                // Key activation result intent is obtained
                int errorCode = intent.getIntExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE);

                if (errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
                    // Key activated or deactivated successfully
                    showToast(context, R.string.klm_action_successful);
                    Log.w(TAG,TAG2 + context.getString(R.string.klm_action_successful));
                    return;
                } else {
                    // activation failed
                    switch (errorCode) {
                        case KnoxEnterpriseLicenseManager.ERROR_INTERNAL:
                            msg_res = R.string.err_klm_internal;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER:
                            msg_res = R.string.err_klm_internal_server;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE:
                            msg_res = R.string.err_klm_licence_invalid_license;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME:
                            msg_res = R.string.err_klm_invalid_package_name;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED:
                            msg_res = R.string.err_klm_licence_terminated;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED:
                            msg_res = R.string.err_klm_network_disconnected;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL:
                            msg_res = R.string.err_klm_network_general;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE:
                            msg_res = R.string.err_klm_not_current_date;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS:
                            msg_res = R.string.err_klm_null_params;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_UNKNOWN:
                            msg_res = R.string.err_klm_unknown;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT:
                            msg_res = R.string.err_klm_user_disagrees_license_agreement;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_DEACTIVATED:
                            msg_res = R.string.err_klm_license_deactivated;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_EXPIRED:
                            msg_res = R.string.err_klm_license_expired;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED:
                            msg_res = R.string.err_klm_license_quantity_exhausted;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_ACTIVATION_NOT_FOUND:
                            msg_res = R.string.err_klm_license_activation_not_found;
                            break;
                        case KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE:
                            msg_res = R.string.err_klm_license_quantity_exhausted_on_auto_release;
                            break;

                        default:
                            // Unknown error code
                            String errorStatus = intent.getStringExtra(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                            String msg = context.getResources().getString(R.string.err_klm_code_unknown, Integer.toString(errorCode), errorStatus);
                            showToast(context, msg);
                            Log.w(TAG,TAG2 + msg);
                            return;
                    }

                    // Display KLM error message
                    showToast(context, msg_res);
                    Log.w(TAG,TAG2 + context.getString(msg_res));
                    return;
                }
            } else if (action.equals(EnterpriseLicenseManager.ACTION_LICENSE_STATUS)) {
                // Backwards-compatible key activation result Intent is obtained
                int errorCode = intent.getIntExtra(EnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, DEFAULT_ERROR_CODE);

                if (errorCode == EnterpriseLicenseManager.ERROR_NONE) {
                    // Backwards-compatible key activated successfully
                    showToast(context, R.string.elm_action_successful);
                    Log.w(TAG,TAG2 + context.getString(R.string.elm_action_successful));
                    return;
                } else {
                    // Backwards-compatible key activation failed
                    switch (errorCode) {
                        case EnterpriseLicenseManager.ERROR_INTERNAL:
                            msg_res = R.string.err_elm_internal;
                            break;
                        case EnterpriseLicenseManager.ERROR_INTERNAL_SERVER:
                            msg_res = R.string.err_elm_internal_server;
                            break;
                        case EnterpriseLicenseManager.ERROR_INVALID_LICENSE:
                            msg_res = R.string.err_elm_licence_invalid_license;
                            break;
                        case EnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME:
                            msg_res = R.string.err_elm_invalid_package_name;
                            break;
                        case EnterpriseLicenseManager.ERROR_LICENSE_TERMINATED:
                            msg_res = R.string.err_elm_licence_terminated;
                            break;
                        case EnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED:
                            msg_res = R.string.err_elm_network_disconnected;
                            break;
                        case EnterpriseLicenseManager.ERROR_NETWORK_GENERAL:
                            msg_res = R.string.err_elm_network_general;
                            break;
                        case EnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE:
                            msg_res = R.string.err_elm_not_current_date;
                            break;
                        case EnterpriseLicenseManager.ERROR_NULL_PARAMS:
                            msg_res = R.string.err_elm_null_params;
                            break;
                        case EnterpriseLicenseManager.ERROR_SIGNATURE_MISMATCH:
                            msg_res = R.string.err_elm_sig_mismatch;
                            break;
                        case EnterpriseLicenseManager.ERROR_UNKNOWN:
                            msg_res = R.string.err_elm_unknown;
                            break;
                        case EnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT:
                            msg_res = R.string.err_elm_user_disagrees_license_agreement;
                            break;
                        case EnterpriseLicenseManager.ERROR_VERSION_CODE_MISMATCH:
                            msg_res = R.string.err_elm_ver_code_mismatch;
                            break;

                        default:
                            // Unknown error code
                            String errorStatus = intent.getStringExtra(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
                            String msg = context.getResources().getString(R.string.err_elm_code_unknown, Integer.toString(errorCode), errorStatus);
                            showToast(context, msg);
                            Log.w(TAG,TAG2 + msg);
                            return;
                    }

                    // Display backwards-compatible key error message
                    showToast(context, msg_res);
                    Log.w(TAG,TAG2 + context.getString(msg_res));
                    return;
                }
            }
        }
    }
}