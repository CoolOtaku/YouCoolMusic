package com.example.youcoolmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;
    Video video;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
        if(App.positionArr > App.videos.size()-1){
            App.positionArr = 0;
        }
        video = App.videos.get(App.positionArr);
        switch(intent.getAction()){
            case "exit":
                clouseMusic();
                break;
            case "pause_play":
                Pause_Play();
                break;
            case "back_music":
                BackVideo();
                break;
            case "next_music":
                NextVideo();
                break;
            case "in_window":
                App.is_Hide_Video = false;
                App.backgroundVideo.setVisibility(View.VISIBLE);
                App.button_bar.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void StartVideo (){
        if(App.positionArr > App.videos.size()-1){
            App.positionArr = 0;
        }
        video = App.videos.get(App.positionArr);
        App.backgroundVideo.loadUrl("https://youcoolmusicvideo.000webhostapp.com/?id="+video.getId());
        new BackgroundMusicService().showNote(context,video.getTitle(),android.R.drawable.ic_media_pause);
        App.play_pause_music.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    private void Pause_Play(){
        int p;
        if(App.is_Play){
            App.backgroundVideo.loadUrl("javascript: player.pauseVideo();");
            p = android.R.drawable.ic_media_play;
            App.is_Play = false;
        }else{
            App.backgroundVideo.loadUrl("javascript: player.playVideo();");
            p = android.R.drawable.ic_media_pause;
            App.is_Play = true;
        }
        new BackgroundMusicService().showNote(context,video.getTitle(),p);
        App.play_pause_music.setBackgroundResource(p);
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
        App.backgroundVideo.destroy();
        App.windowManager.removeView(App.view);
        notificationManager.cancel(1337);
        Intent intent1 = new Intent(context, BackgroundMusicService.class);
        context.stopService(intent1);
    }

}
