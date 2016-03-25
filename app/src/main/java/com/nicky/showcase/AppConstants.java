package com.nicky.showcase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;

import com.example.nicholas.myapplication.backend.postApi.PostApi;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * Created by Nicholas on 10/8/2015.
 */
public class AppConstants {

    public static final String WEB_CLIENT_ID = "409879316400-qde8a0vbc0b0k8qm0l2c4vnsjkq00k8c.apps.googleusercontent.com";

    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

    public static final JsonFactory JSON_FACT = new AndroidJsonFactory();

    public static final HttpTransport HTTP_TRANS = AndroidHttp.newCompatibleTransport();

    public static PostApi getApiHandle(@Nullable GoogleAccountCredential credential) {
        PostApi.Builder user = new PostApi.Builder(AppConstants.HTTP_TRANS,
                AppConstants.JSON_FACT, credential)
                .setApplicationName("com.nicky.showcase");
        return user.build();
    }

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
}
