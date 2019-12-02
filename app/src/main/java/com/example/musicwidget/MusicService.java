package com.example.musicwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private String filename;

    Song song;

    private static final String TAG = "Music Service";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction() != null ? intent.getAction() : "";

        // verify if the call action is from the Mainactivy by checking if the filename is not null
        if(intent.getStringExtra("filename") != null) {
            filename = intent.getStringExtra("filename");
            song  = (Song)intent.getExtras().get("song");

            Log.d("song", song.getArtist());

            try {
                if(mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(filename);
                mMediaPlayer.prepare();
            } catch (Exception e){

            }
        }
        else
            filename = MainActivity.filename;

        Log.d("action", action);

        try {
            if (action.equals(MusicWidgetClass.ACTION_PLAY)) {
                if (mMediaPlayer.isPlaying())
//                    pauseMusic();
                    ;
                else{
                    playMusic();
                }
            }
            else if (action.equals(MusicWidgetClass.ACTION_PAUSE)) {
                if(mMediaPlayer.isPlaying())
                    pauseMusic();
                else
                    Toast.makeText(this, "no music is playing", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, START_STICKY, 1);
    }

    private void playMusic()
    {
        Log.d(TAG, "PLAY");

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }else{
//            mMediaPlayer.setSong(song);
//            mMediaPlayer.play();
        }

        updateUI(song.getTitle(), song.getArtist(), song.getDurationStr(), getAUdioBitMap(song.getBitmap()));
        Log.i("Music Service", "Playing: " + song.getTitle());
    }


    private void pauseMusic()
    {
        Log.d(TAG, "PAUSE");
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            Log.d(TAG, "Music paused");
        }

        updateUI(song.getTitle(), song.getArtist(), song.getDurationStr(), getAUdioBitMap(song.getBitmap()));
        Log.i("Music Service", "Playing: " + song.getTitle());
    }

    private void updateUI(String title, String artist, String duration, Bitmap bitmap){

        RemoteViews remoteViews = MusicWidgetClass.getRemoteViews(this);

        if (title != null && artist != null && duration != null){
            remoteViews.setTextViewText(R.id.title, title);
            remoteViews.setTextViewText(R.id.artist, artist);
            remoteViews.setTextViewText(R.id.duration, duration);

            if(bitmap != null)
                 remoteViews.setImageViewBitmap(R.id.imageview, bitmap);
        } else {
            remoteViews.setViewVisibility(R.id.title, View.GONE);
        }

        ComponentName thisWidget = new ComponentName(this, MusicWidgetClass.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, remoteViews);
    }

    private static Bitmap getAUdioBitMap(byte[] art) {


        Bitmap bm = null;

        byte[] artBytes =  art;
        if(artBytes!=null) {
            bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
        }

        return bm;

    }

}
