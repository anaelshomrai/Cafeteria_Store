package com.cafeteria.cafeteria_store;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

//import com.onesignal.OSNotificationOpenResult;
//import com.onesignal.OneSignal;

import java.util.Locale;

/**
 * Created by Shira Elitzur on 22/11/2016.
 */

public class App extends Application {
    private static Configuration configuration;
    public static  String language = "iw";

    @Override
    public void onCreate() {
        super.onCreate();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT < 17){
            config.locale = locale;
        }else{
            config.setLocale(locale);
        }
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        // initialize OneSignal for handling push notifications
//        OneSignal.startInit(this).setNotificationOpenedHandler(new MyNotificationOpenedHandler()).init();
//        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
//            @Override
//            public void idsAvailable(String userId, String registrationId) {
//                Log.d("debug", "Device Token:" + userId);
//            }
//        });
    }

//    private class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
//        // This fires when a notification is opened by tapping on it.
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result) {
//            JSONObject data = result.notification.payload.additionalData;
//            Log.e("DEBUG","Notification Result : "+data.toString());
//            String ordersJson;
//            ordersJson = data.optString("orders", null);
//
//            Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
//            intent.putExtra("orders_list",ordersJson);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT < 17){
            config.locale = locale;
        }else{
            config.setLocale(locale);
        }
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

}
