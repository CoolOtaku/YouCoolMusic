package com.example.youcoolmusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BackgroundMusicService extends Service implements View.OnClickListener {

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    View view;
    WebView backgroundVideo;
    Video video;
    private NotificationManagerCompat notificationManager;

    Button clouse_music;
    Button back_music;
    Button play_pause_music;
    Button next_music;
    Button in_statusBar;
    LinearLayout button_bar;
    LinearLayout ln;

    private int xDelta, yDelta;
    boolean is_full_screen = false;
    final String[] second = {""};
    boolean is_sek_to = false;

    @Override
    public void onCreate(){
        super.onCreate();
        App.runingService = true;
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.castom_noty, null);
        if(App.layoutParams != null){
            layoutParams = App.layoutParams;
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }else{
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

            layoutParams.gravity = Gravity.CENTER | Gravity.CENTER;
            layoutParams.x = 0;
            layoutParams.y = 0;
        }
        windowManager.addView(view, layoutParams);

        backgroundVideo = (WebView) view.findViewById(R.id.backgroundVideo);
        backgroundVideo.setWebViewClient(new MyWebViewClient());
        backgroundVideo.setWebChromeClient(new ChromeClient1());
        WebSettings webSettings = backgroundVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.137 Safari/537.36");

        clouse_music = (Button) view.findViewById(R.id.clouse_music);
        back_music = (Button) view.findViewById(R.id.back_music);
        play_pause_music = (Button) view.findViewById(R.id.play_pause_music);
        next_music = (Button) view.findViewById(R.id.next_music);
        in_statusBar = (Button) view.findViewById(R.id.in_statusBar);
        button_bar = (LinearLayout) view.findViewById(R.id.button_bar);
        ln = (LinearLayout) view.findViewById(R.id.ln);

        if(App.is_Hide_Video){
            hide_window();
        }

        clouse_music.setOnClickListener(this);
        play_pause_music.setOnClickListener(this);
        back_music.setOnClickListener(this);
        next_music.setOnClickListener(this);
        in_statusBar.setOnClickListener(this);

        StartVideo();
        copy_obj();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        WindowManager.LayoutParams lParams = (WindowManager.LayoutParams) view.getLayoutParams();
                        xDelta = x - lParams.x;
                        yDelta = y - lParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                         layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                            layoutParams.x = x - xDelta;
                            layoutParams.y = y - yDelta;
                            layoutParams.gravity=Gravity.NO_GRAVITY;
                            view.setLayoutParams(layoutParams);
                            windowManager.updateViewLayout(view,layoutParams);
                            App.layoutParams = layoutParams;
                        break;
                    }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_music:
                BackVideo();
                break;
            case R.id.play_pause_music:
                Pause_Play();
                break;
            case R.id.next_music:
                NextVideo();
                break;
            case R.id.in_statusBar:
                hide_window();
                copy_obj();
                break;
            case R.id.clouse_music:
                clouseMusic();
                break;
        }
    }

    private void hide_window(){
        App.is_Hide_Video = true;
        backgroundVideo.setVisibility(View.GONE);
        button_bar.setVisibility(View.GONE);
    }

    private void StartVideo (){
        if(App.positionArr > App.videos.size()-1){
            App.positionArr = 0;
        }
        video = App.videos.get(App.positionArr);
        backgroundVideo.loadUrl("https://youcoolmusicvideo.000webhostapp.com/?id="+video.getId());
        showNote(BackgroundMusicService.this,video.getTitle(),android.R.drawable.ic_media_pause);
        play_pause_music.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    private void Pause_Play(){
        int p;
        if(App.is_Play){
            backgroundVideo.loadUrl("javascript: player.pauseVideo();");
            p = android.R.drawable.ic_media_play;
            App.is_Play = false;
        }else{
            backgroundVideo.loadUrl("javascript: player.playVideo();");
            p = android.R.drawable.ic_media_pause;
            App.is_Play = true;
        }
        showNote(BackgroundMusicService.this,video.getTitle(),p);
        play_pause_music.setBackgroundResource(p);
    }

    private void NextVideo(){
        App.is_Stop = false;
        if (App.positionArr+1 != App.videos.size()){
            App.positionArr++;
        }else{
            App.positionArr=0;
        }
        StartVideo();
    }

    private void BackVideo(){
        App.is_Stop = false;
        if(App.positionArr == 0){
            App.positionArr = App.videos.size()-1;
        }else{
            App.positionArr--;
        }
        StartVideo();
    }

    private void clouseMusic(){
        App.is_Stop = false;
        App.runingService = false;
        backgroundVideo.destroy();
        windowManager.removeView(view);
        notificationManager.cancel(1337);
        stopForeground(true);
        stopSelf();
    }

    private void copy_obj(){
        App.backgroundVideo = backgroundVideo;
        App.windowManager = windowManager;
        App.view = view;
        App.button_bar = button_bar;
        App.play_pause_music = play_pause_music;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification1 =
                new NotificationCompat.Builder(BackgroundMusicService.this, App.CHANEL_ID)
                        .setSmallIcon(R.drawable.animetan).build();
        startForeground(1336,notification1);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("DestroyMusic");
        super.onDestroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(App.is_Stop){
                NextVideo();
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            App.is_Play = true;
            play_pause_music.setBackgroundResource(android.R.drawable.ic_media_pause);
            if(!App.is_Stop && NetWork.hasConnection(BackgroundMusicService.this)){
                App.is_Stop = true;
            }
            if(is_sek_to){
                backgroundVideo.loadUrl("javascript: player.seekTo("+second[0]+", true);");
                is_sek_to = false;
            }
        }
    }
    private class ChromeClient1 extends WebChromeClient {
        @Override
        public void onHideCustomView(){
        }
        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback){
            App.is_Stop = false;
            WindowManager.LayoutParams lP = (WindowManager.LayoutParams) view.getLayoutParams();
            lP.x = 0;
            lP.y = 0;
            lP.gravity = Gravity.CENTER|Gravity.CENTER;
            backgroundVideo.setVisibility(View.GONE);
            if(is_full_screen){
                is_full_screen = false;
                button_bar.setVisibility(View.VISIBLE);
                lP.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lP.width = WindowManager.LayoutParams.WRAP_CONTENT;
                backgroundVideo.setLayoutParams(new RelativeLayout.LayoutParams(getPx(260),getPx(130)));
            }else{
                is_full_screen = true;
                button_bar.setVisibility(View.GONE);
                int orientation = BackgroundMusicService.this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    lP.height = App.height;
                    lP.width = App.width;
                } else {
                    lP.height = App.width;
                    lP.width = App.height;
                }
                backgroundVideo.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
            backgroundVideo.setVisibility(View.VISIBLE);
            view.setLayoutParams(lP);
            windowManager.updateViewLayout(view, lP);
            backgroundVideo.evaluateJavascript(
                    "(function() { var ct = player.getCurrentTime();\n" +
                            "return ct; })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String v) {
                        second[0] = v;
                        }});
            is_sek_to = true;
            backgroundVideo.reload();
        }
    }
    private int getPx(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return((int) (dp * scale + 0.5f));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(is_full_screen){
            WindowManager.LayoutParams lP = (WindowManager.LayoutParams) view.getLayoutParams();
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                lP.width = App.height;
                lP.height = App.width;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                lP.height = App.height;
                lP.width = App.width;
            }
            view.setLayoutParams(lP);
            windowManager.updateViewLayout(view, lP);
        }
        super.onConfigurationChanged(newConfig);
    }
    public void showNote(Context context,String text, int playIcon){
        notificationManager = NotificationManagerCompat.from(context);
        Intent Ia1 = new Intent(context, NotificationReceiver.class).setAction("exit");
        PendingIntent pIa1  = PendingIntent.getBroadcast(context,0,Ia1,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia2 = new Intent(context, NotificationReceiver.class).setAction("pause_play");
        PendingIntent pIa2  = PendingIntent.getBroadcast(context,0,Ia2,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia3 = new Intent(context, NotificationReceiver.class).setAction("back_music");
        PendingIntent pIa3  = PendingIntent.getBroadcast(context,0,Ia3,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia4 = new Intent(context, NotificationReceiver.class).setAction("next_music");
        PendingIntent pIa4  = PendingIntent.getBroadcast(context,0,Ia4,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia5 = new Intent(context, NotificationReceiver.class).setAction("in_window");
        PendingIntent pIa5  = PendingIntent.getBroadcast(context,0,Ia5,PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap artwork = BitmapFactory.decodeResource(context.getResources(),R.drawable.animetan);

        Notification notification =
                new NotificationCompat.Builder(context, App.CHANEL_ID)
                        .setSmallIcon(R.drawable.animetan)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(text)
                        .setLargeIcon(artwork)
                        .addAction(android.R.drawable.ic_media_previous,"back_music",pIa3)
                        .addAction(playIcon,"pause_play",pIa2)
                        .addAction(android.R.drawable.ic_media_next,"next_music",pIa4)
                        .addAction(android.R.drawable.ic_menu_view,"in_window",pIa5)
                        .addAction(android.R.drawable.ic_menu_close_clear_cancel,"exit",pIa1)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0,1,2))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true).build();

        notificationManager.notify(1337, notification);
    }

}
