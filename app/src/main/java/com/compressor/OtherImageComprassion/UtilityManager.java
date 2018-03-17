package com.compressor.OtherImageComprassion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityManager {

    private static final UtilityManager INSTANCE = new UtilityManager();
    private static final String TAG = UtilityManager.class.getSimpleName();

    private UtilityManager() {
    }

    public static UtilityManager getInstance() {
        return INSTANCE;
    }

    public static boolean isConnected(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                    System.out.println("WIFI CONNECTION AVAILABLE");
                } else {
                    System.out.println("WIFI CONNECTION NOT AVAILABLE");
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                    System.out.println("MOBILE INTERNET CONNECTION AVAILABLE");
                } else {
                    System.out.println("MOBILE INTERNET CONNECTION NOT AVAILABLE");
                }
            }
        }
        Log.v(TAG, "Connection avail WIFI : " + haveConnectedWifi + "\n" + "Mobile : " + haveConnectedMobile);
        return haveConnectedWifi || haveConnectedMobile;
    }

    @SuppressWarnings("deprecation")
    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isEmpty(EditText edittext) {
        if (edittext.getText().toString().trim().length() != 0) {
            return false;
        }
        return true;
    }

    public static boolean isEmptyTextView(TextView textView) {
        if (textView.getText().toString().trim().length() != 0) {
            return false;
        }
        return true;
    }

}