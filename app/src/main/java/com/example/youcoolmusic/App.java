package com.example.youcoolmusic;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class App extends Application {

    public static final String CHANEL_ID = "YouCoolMusic1337";

    public static ArrayList<Video> videos;
    public static int positionArr;
    public static boolean runingService = false;
    public static boolean is_Play = false;
    public static boolean is_Stop = false;
    public static boolean is_Hide_Video = false;

    public static WebView backgroundVideo;
    public static WindowManager windowManager;
    public static WindowManager.LayoutParams layoutParams;
    public static View view;
    public static LinearLayout button_bar;
    public static Button play_pause_music;

    public static int width;
    public static int height;

    @Override
    public void onCreate(){
        super.onCreate();
        createNotifiChanel();
    }

    private void createNotifiChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID,"YouCoolMusic", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
