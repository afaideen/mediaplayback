package com.app.han.mediaplayback;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by han on 12/16/2017.
 */

public class PlayListActivity extends AppCompatActivity {
    private ArrayList<File> listSongs = new ArrayList<>();
    String[] items;
    ListView lvPlaylist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        lvPlaylist = (ListView) findViewById(R.id.lvPlaylist);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        listSongs = (ArrayList<File>) b.get("ListSongs");
        items = new String[listSongs.size()];
        for (int i = 0; i < listSongs.size(); i++) {
            items[i] = listSongs.get(i).getName().replace(".mp3", "");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),R.layout.mytemplate,R.id.songTitle,items);
        lvPlaylist.setAdapter(adp);
        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(PlayListActivity.this, MainActivity.class);
                i.putExtra("pos", position);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                // Closing PlayListView
                finish();
            }
        });
    }


}
