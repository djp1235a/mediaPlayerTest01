package com.example.administrator.homework02test2app;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static ArrayList<MusicMedia> musicList;
    private ListView listView;
    private TextView textView;
    public static int currentposition=-1;
    private Intent intent;
    private boolean isplay = false;
    private boolean isserviceruning=false;
    private MediaPlayerService mediaPlayerService;
    private MediaPlayer mediaPlayer;
    private ImageView btn_play_pause ;
    private SeekBar musicseekBar;
    private Handler handler;
    private ImageView previous;
    private ImageView next;
    private MusicAdapter musicAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    private void init(){
        intent = new Intent(this,MediaPlayerService.class);

        textView = findViewById(R.id.musicinfo);
        btn_play_pause = findViewById(R.id.play_pause);
        musicList  = scanAllMusicFiles();
        listView = findViewById(R.id.musiclistview);
        musicAdapter= new MusicAdapter(this,R.layout.songitem_layout,musicList);
        listView.setAdapter(musicAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                currentposition = position;
                Log.i("djp","222"+currentposition);
                musicseekBar.setProgress(0);
                player(currentposition);
            }
        });
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMusic();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });
        handler = new Handler();
        if (mediaPlayer!=null){
            reinit();
        }
        musicseekBar = findViewById(R.id.seekbar);
        musicseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                if (fromuser){
                    btn_play_pause.setBackgroundResource(R.drawable.pause);
                    mediaPlayerService.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }

            }
        });




    }

    private void reinit(){
        isserviceruning=true;
        if (mediaPlayerService.getMediaPlayer().isPlaying()){
            isplay=true;
            btn_play_pause.setBackgroundResource(R.drawable.pause);
        }
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    public ArrayList<MusicMedia> scanAllMusicFiles(){
        ArrayList<MusicMedia> mylist = new ArrayList<MusicMedia>();

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (size>1024*800){
                    MusicMedia musicMedia = new MusicMedia();
                    musicMedia.setId(id);
                    musicMedia.setTitle(title);
                    musicMedia.setArtist(artist);
                    musicMedia.setDuration(duration);
                    musicMedia.setUrl(url);
                    musicMedia.setSize(size);

                    mylist.add(musicMedia);
                }
                cursor.moveToNext();
            }

        }
        return mylist;

    }


    public void play_pause(View view) {
        Log.i("djp","play_pause");
        if(isserviceruning) {
            if (isplay) {
                pause();
            } else {
                Log.i("djp","elsele11??/");
                player1("2");
            }
        }
            else {
            if (isplay){
            pause();
        }else {

            player1("2");
                Log.i("djp","else2??");
        }
    }
    }


    private void player(){
        player(currentposition);

    }
    @SuppressLint("SetTextI18n")
    private void player(int position){
       textView.setText(musicList.get(position).getTitle()+"   playing.....");
        intent.putExtra("curposition",position);
        intent.putExtra("url",musicList.get(position).getUrl());
        intent.putExtra("MSG","0");
        isplay=true;
        btn_play_pause.setBackgroundResource(R.drawable.pause);
        startService(intent);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        Log.i("djp","bindservice");

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mediaPlayerService=((MediaPlayerService.musicBinder)service).getPlayInfo();
            mediaPlayer=mediaPlayerService.getMediaPlayer();
            currentposition=mediaPlayerService.getCurposition();
        Log.i("djp","333:..."+currentposition);
          new Thread() {
              @Override
              public void run() {

                  while (mediaPlayerService != null)

                  {
                      try {
                          Thread.sleep(1000);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      musicseekBar.setProgress(mediaPlayerService.getCurrentPosition());

                  }

              }
          }.start();
          handler.post(seekBarHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mediaPlayerService = null;
        }
    };


   Runnable seekBarHandler = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {

            musicAdapter.setSelectedPosition(currentposition);
            musicAdapter.notifyDataSetChanged();
            musicseekBar.setProgress(mediaPlayerService.getCurrentPosition());
            musicseekBar.setMax(mediaPlayerService.getDuration());
            Log.i("djp","111"+currentposition);
            textView.setText(musicList.get(currentposition).getTitle()+"    " +
                    mediaPlayerService.toTime(mediaPlayerService.getCurrentPosition())+
                    "/"+mediaPlayerService.toTime(mediaPlayerService.getDuration()));

            handler.postDelayed(seekBarHandler, 1000);

        }
    };

    private void player1(String info){
        Log.i("djp","play11???" );
        intent.putExtra("MSG",info);
        Log.i("djp","play22???" );
        isplay=true;
        Log.i("djp","play33???" );
        //换按钮图片
        btn_play_pause.setBackgroundResource(R.drawable.pause);
        startService(intent);
        Log.i("djp","play44???" );
    }
    private void pause(){
        intent.putExtra("MSG","1");
        isplay=false;
        Log.i("djp","pause???" );
        //换按钮图片
        btn_play_pause.setBackgroundResource(R.drawable.play);
        startService(intent);
    }

    private int previousMusic(){
        if (currentposition>0){
            currentposition-=1;

        }
        else {
            currentposition=musicList.size()-1;
        }
        player();
        return currentposition;
    }

    private int nextMusic(){
        if (currentposition<musicList.size()-2){
            currentposition+=1;

        }else {
            currentposition=0;
        }
        player();

        return currentposition;
    }

    @Override
    protected void onResume() {
        if(isplay==true){
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
        }
        super.onResume();

    }

    @Override    protected void onPause() {
        super.onPause();
    Log.i("MusicPlayerService", "MusicActivity...onPause........." + Thread.currentThread().hashCode());        //绑定服务了
        if(mediaPlayerService != null)
         {            unbindService(conn);
        }

         handler.removeCallbacks(seekBarHandler);
    }
         @Override
         protected void onDestroy() {
             super.onDestroy();
             Log.i("MusicPlayerService", "MusicActivity...onDestroy........." + Thread.currentThread().hashCode());
            unbindService(conn);
         }

    @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);

        }
        return super.onKeyDown(keyCode, event);
    }
}

