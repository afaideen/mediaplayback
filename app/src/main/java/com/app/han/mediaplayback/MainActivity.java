package com.app.han.mediaplayback;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import static android.media.ToneGenerator.MAX_VOLUME;

//ref http://mrbool.com/how-to-play-audio-files-in-android-with-a-seekbar-feature-and-mediaplayer-class/28243
// download sample.mp3 at http://sharelagu.info/site_view.xhtml?cmid=13248785&get-artist=Richard%20Marx&get-title=Right%20Here%20Waiting%20For%20You
//check project AndroidBuildingMusicPlayer1
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    SeekBar volume,seek_bar;
    ImageButton btnPlaylist,btnPlay,btnRepeat,btnShuffle;
//    Button pause_button;
    MediaPlayer player;
    TextView songTitle, text_shown,songCurrentDurationLabel,songTotalDurationLabel;
    Handler seekHandler = new Handler();
    private boolean isRepeat = false;
    private boolean isShuffle = false;
    private static ArrayList<File> listSongs = new ArrayList<>();
    private int soundVolume = 30;
    private float volumeStrength;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getInit();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Getting all songs list
        listSongs = findSongs(Environment.getExternalStorageDirectory());
    }

    private ArrayList<File> findSongs(File root) {
        ArrayList<File> listFile = new ArrayList<>();
        File[] files = root.listFiles();
        for(File file : files){
            if(file.isDirectory() && !file.isHidden()){
                listFile.addAll(findSongs(file));
            }else {
                if(file.getName().endsWith(".mp3") ){//|| file.getName().endsWith(".wav")){
                    listFile.add(file);
                }
            }

        }
        return listFile;

    }

    public void getInit() throws URISyntaxException {
        seek_bar = (SeekBar) findViewById(R.id.songProgressBar);
        volume = (SeekBar) findViewById(R.id.volume);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                i.putExtra("ListSongs", listSongs);
                startActivityForResult(i, 100);
            }
        });
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        text_shown = (TextView) findViewById(R.id.text_shown);
        songTitle = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        btnPlay.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            int position = (int) b.get("pos");
            Uri uri = Uri.parse(listSongs.get(position).toString());
            player = MediaPlayer.create(this, uri);
            fileName = listSongs.get(position).getName().replace(".mp3", "");
        }
        else {
            player = MediaPlayer.create(this, R.raw.sample);
            fileName = "sample";
        }
        songTitle.setText(fileName);

        seek_bar.setProgress(0);
        seek_bar.setMax(100);
        volume.setMax(100);
        volume.setProgress(soundVolume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                soundVolume = volume.getProgress();
                volumeStrength = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
                player.setVolume(volumeStrength, volumeStrength);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seek_bar.setOnSeekBarChangeListener(this);
        player.setOnCompletionListener(this); // Important

        volumeStrength = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
        player.setVolume(volumeStrength, volumeStrength);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        player.release();
    }


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
            case R.id.btnShuffle:
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
                break;
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
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        player.seekTo(currentPosition);

        updateProgressBar();
    }



    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        seekHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Utilities utils = new Utilities();
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = player.getDuration();
            long currentDuration = player.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            seek_bar.setProgress(progress);

            // Running this thread after 100 milliseconds
            seekHandler.postDelayed(this, 100);
        }
    };



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
        else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            int currentSongIndex = rand.nextInt((listSongs.size() - 1) - 0 + 1) + 0;
//            playSong(currentSongIndex);

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
