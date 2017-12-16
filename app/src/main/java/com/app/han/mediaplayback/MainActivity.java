package com.app.han.mediaplayback;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

//ref http://mrbool.com/how-to-play-audio-files-in-android-with-a-seekbar-feature-and-mediaplayer-class/28243
// download sample.mp3 at http://sharelagu.info/site_view.xhtml?cmid=13248785&get-artist=Richard%20Marx&get-title=Right%20Here%20Waiting%20For%20You
//check project AndroidBuildingMusicPlayer1
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    SeekBar seek_bar;
    ImageButton btnPlay,btnRepeat,btnShuffle;
//    Button pause_button;
    MediaPlayer player;
    TextView text_shown,songCurrentDurationLabel,songTotalDurationLabel;
    Handler seekHandler = new Handler();
    private MediaPlayer.TrackInfo[] trackInfo;
    private boolean isRepeat = false;
    private boolean isShuffle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getInit();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        seekUpdation();
    }

    public void getInit() throws URISyntaxException {
        seek_bar = (SeekBar) findViewById(R.id.songProgressBar);//(R.id.seek_bar);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
//        pause_button = (Button) findViewById(R.id.pause_button);
        text_shown = (TextView) findViewById(R.id.text_shown);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        btnPlay.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
//        pause_button.setOnClickListener(this);
        player = MediaPlayer.create(this, R.raw.sample);

//        seek_bar.setMax(player.getDuration());
        // set Progress bar values
        seek_bar.setProgress(0);
        seek_bar.setMax(100);

        seek_bar.setOnSeekBarChangeListener(this);
        player.setOnCompletionListener(this); // Important

  /*      String path = "android.resource://" + getPackageName() + "/" + R.raw.sample;
      *//*  Uri url = Uri.parse(path);
        File file = new File(url.toString());
        path = file.getPath();*//*

        try {
            path = new File(new URI(path).getPath()).getCanonicalPath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Cursor c = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TRACK,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.YEAR
                },
                MediaStore.Audio.Media.DATA + " = ?",
                new String[] {
                        path
                },
                "");

        if (null == c) {
            // ERROR
        }

        while (c.moveToNext()) {
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.YEAR));
        }*/
    }

/*    Runnable run = new Runnable() {
        @Override public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        seek_bar.setProgress(player.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }*/


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:

                // check for already playing
                if(player.isPlaying()){
                    if(player!=null){
                        player.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                    text_shown.setText("Paused...");
                }else{
                    // Resume song
                    if(player!=null){
                        playSong();
                    }


                }


                break;
            case R.id.btnRepeat:
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
                break;
//            case R.id.pause_button:
//                player.pause();
//                text_shown.setText("Paused...");
        }

    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = player.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        player.seekTo(currentPosition);

//        seekUpdation();
        updateProgressBar();
    }



    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        seekHandler.postDelayed(mUpdateTimeTask, 100);
    }
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = player.getDuration();
            long currentDuration = player.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seek_bar.setProgress(progress);

            // Running this thread after 100 milliseconds
            seekHandler.postDelayed(this, 100);
        }
    };

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        seekHandler.removeCallbacks(mUpdateTimeTask);
        text_shown.setText("");
        // set Progress bar values
        seek_bar.setProgress(0);
        // Changing button image to play button
        btnPlay.setImageResource(R.drawable.btn_play);
        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong();
        }
    }

    private void playSong() {
        player.start();
        // Changing button image to pause button
        btnPlay.setImageResource(R.drawable.btn_pause);
        text_shown.setText("Playing...");

        // Updating progress bar
        updateProgressBar();

    }
}
