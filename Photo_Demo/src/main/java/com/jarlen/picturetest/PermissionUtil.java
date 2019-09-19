package com.jarlen.picturetest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by zhanghongbiao@vargo.com.cn on 18-4-11.
 */

public class PermissionUtil {

    public static final int PERMISSIONS_CODE = 1;


    public static boolean isGrantAccessCoarseLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    public static void requestAccessCoarseLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSIONS_CODE);
        }
    }

    public static boolean isGrantWriteExternalStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }
    public static void requestWriteExternalStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, PERMISSIONS_CODE);
        }
    }

    private static final String[] defaultAllPermissions = {
        Manifest.permission.CAMERA,
        //Manifest.permission.ACCESS_COARSE_LOCATION,
        //Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //Manifest.permission.READ_PHONE_STATE,
        //Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_CALENDAR
    };

    public static boolean isGrantAllPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String s: defaultAllPermissions){
                 if(context.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                     return false;
            }
        }

        return true;
    }


    public static void requestAllPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(defaultAllPermissions, PERMISSIONS_CODE);
        }
    }

}