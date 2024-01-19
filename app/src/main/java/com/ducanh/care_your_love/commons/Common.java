package com.ducanh.care_your_love.commons;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.ducanh.care_your_love.LoginActivity;
import com.ducanh.care_your_love.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Common {
    public static String getTimestampNow() {
        return new Timestamp(new Date().getTime()).toString();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(activity.getCurrentFocus() != null ? activity.getCurrentFocus() : new View(activity), InputMethodManager.SHOW_IMPLICIT);
    }

    public static void logout(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        Store.userLogin = null;
    }

    public static void setFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Store.userLogin.fcmToken = task.getResult();
                FirebaseDatabase.getInstance().getReference(User.REFERENCE_NAME).child(Store.userLogin.uuid).setValue(Store.userLogin);
            }
        });
    }
}
