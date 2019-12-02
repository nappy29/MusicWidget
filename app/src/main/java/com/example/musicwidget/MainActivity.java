package com.example.musicwidget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView musiclist;
    Cursor cursor;
    int music_column_index;
    int count;
    MediaPlayer mMediaPlayer;


    MediaMetadataRetriever mediaMetadataRetriever;

    public static String filename;
    private byte[] imageStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaMetadataRetriever = new MediaMetadataRetriever();

        if(Utils.isStoragePermissionGranted(this))
            getMusicFromLibrary();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMusicFromLibrary() {
        System.gc();
        String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE };

        cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);

        count = cursor.getCount();
        musiclist = findViewById(R.id.listview);
        musiclist.setAdapter(new MusicAdapter(getApplicationContext()));

        musiclist.setOnItemClickListener(musicgridlistener);
        mMediaPlayer = new MediaPlayer();
    }

    private AdapterView.OnItemClickListener musicgridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            System.gc();
            music_column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            cursor.moveToPosition(position);
            filename = cursor.getString(music_column_index);

            mediaMetadataRetriever.setDataSource(filename);


            // Retrieve metadata for audio file
            String artist   = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String title    = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if(mediaMetadataRetriever.getEmbeddedPicture() != null)
                imageStr = mediaMetadataRetriever.getEmbeddedPicture();


            Log.d("getLong", getDurationString(duration));
            Song song  = new Song(0, title, artist, getDurationString(duration), imageStr);

            Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
            serviceIntent.setAction(MusicWidgetClass.ACTION_PLAY);
            serviceIntent.putExtra("song", song);
            serviceIntent.putExtra("filename", filename);

            MainActivity.this.startService(serviceIntent);

            finish();

        }
    };

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        mMediaPlayer.stop();
    }

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;

        public MusicAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            TextView tv = new TextView(mContext.getApplicationContext());
            String id = null;
            if (convertView == null) {
                music_column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                cursor.moveToPosition(position);
                id = cursor.getString(music_column_index);
                music_column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                cursor.moveToPosition(position);
                id += " Size(KB):" + cursor.getString(music_column_index);
                tv.setText(id);
            } else
                tv = (TextView) convertView;
            return tv;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

            getMusicFromLibrary();
        }
    }

    private static String getDurationString(String microseconds) {

        long duration = Long.parseLong(microseconds);
        long sec = (duration / 1000) % 60;
        long min = (duration / 1000) / 60;

        return "time = " + min + ":" + sec;

    }


}
