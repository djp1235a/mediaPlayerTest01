package com.example.administrator.homework02test2app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service {
    private static MediaPlayer mediaPlayer;
    private String url;
    private String MSG;
    private  int curposition=-1 ;
    private int currentPosition;
    private int Duration;
    private MusicMedia musicMedia;
    private musicBinder musicbinder;
    private ArrayList<MusicMedia> musiclist;
    private MusicAdapter musicAdapter;

    public MediaPlayerService() {
        musicbinder = new musicBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return musicbinder;
    }

    public class musicBinder extends Binder{
        public MediaPlayerService getPlayInfo(){
            return MediaPlayerService.this;
        }
    }

    public int getCurrentPosition() {

            currentPosition=mediaPlayer.getCurrentPosition();


        return currentPosition;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public MusicMedia getMusicMedia() {
        return musicMedia;
    }

    public  MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public  int getCurposition() {
        return curposition;
    }

    @Override
    public void onCreate() {
        Log.i("'djp","onCreat....2");
        super.onCreate();
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        musiclist = MainActivity.musicList;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                curposition = ++MainActivity.currentposition;
                int p =(curposition) % musiclist.size();
                url =  musiclist.get(p ).getUrl();

                player();

            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("djp","onStartCommand");
        if (intent!=null){
            Log.i("djp","onstartcommand111");
            MSG = intent.getStringExtra("MSG");
            url = intent.getStringExtra("url");
            curposition = intent.getIntExtra("curposition", 0);
            Log.i("djp","onstartcommand222");
            switch (MSG){
                case "0":
                    Log.i("djp", "onstartcommand333");

                    player();
                    break;
                case "2":
                    Log.i("djp", "onstartcommand444");
                    mediaPlayer.start();
                    break;
                case "1":
                    Log.i("djp", "onstartcommand555");
                    mediaPlayer.pause();
                    break;
            }

        }

            return super.onStartCommand(intent, flags, startId);
    }

    private void player(){
        Log.i("djp","player...");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("djp","onunbind...");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("djp","onrebind");
    }

    @Override
    public void onDestroy() {
        Log.i("djp","ondestory...");
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        mediaPlayer=null;
        }
    }
    public String toTime(int time){
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}



