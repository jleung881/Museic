package com.example.justin.museic;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player extends AppCompatActivity implements View.OnClickListener{
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    ArrayList<Integer> adjSongs = new ArrayList<Integer>();

    int position;
    Uri u;
    Thread updateSeekBar;
    ImageView albumArt;

    SeekBar sb;
    Button pause, ff, next, fb, previous, like, dislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        albumArt = (ImageView) findViewById(R.id.albumArt);
        pause = (Button) findViewById(R.id.pause);
        ff = (Button) findViewById(R.id.ff);
        next = (Button) findViewById(R.id.next);
        fb = (Button) findViewById(R.id.fb);
        previous = (Button) findViewById(R.id.previous);
        like = (Button) findViewById(R.id.like);
        dislike = (Button) findViewById(R.id.dislike);

        pause.setOnClickListener(this);
        ff.setOnClickListener(this);
        next.setOnClickListener(this);
        fb.setOnClickListener(this);
        previous.setOnClickListener(this);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);

        sb = (SeekBar) findViewById(R.id.sb);
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try {
                        sleep(500);
                        if (mp.isPlaying())
                            currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }   catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

                //super.run();
            }
        };

        if(mp!=null){
            mp.stop();
            mp.release();
        }

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos", 0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        adjSongs.add(position);
        mp.start();
        sb.setMax(mp.getDuration());


        updateSeekBar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try{
                    mp.stop();
                    mp.reset();
                    position = position + 1;
                    if(position < mySongs.size()) {
                        u = Uri.parse(mySongs.get(position).toString());
                        try {
                            mp.setDataSource(getApplicationContext(), u);
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                        sb.setMax(mp.getDuration());
                        updateSeekBar.start();
                    }
                    else{
                        position = 0;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    };
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.pause:
                if(mp.isPlaying()){
                    pause.setText(">");
                    mp.pause();
                }
                else{
                    mp.start();
                    pause.setText("||");
                }
                break;
            case R.id.ff:
                mp.seekTo(mp.getCurrentPosition()+5000);
                break;
            case R.id.fb:
                mp.seekTo(mp.getCurrentPosition()-5000);
                break;
            case R.id.next:
                mp.stop();
            //    mp.release();
                mp.reset();
                position = (position+1)%mySongs.size();
                u = Uri.parse(mySongs.get(position).toString());
                try {
                    mp.setDataSource(getApplicationContext(), u);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
                sb.setMax(mp.getDuration());
                updateSeekBar.start();
                break;
            case R.id.previous:
                mp.stop();
            //    mp.release();
                mp.reset();
                position = (position-1<0)? mySongs.size()-1: position-1;
                /*if(position-1 <0){
                    position = mySongs.size()-1;
                }
                else {
                    position = position-1;
                }*/
                u = Uri.parse(mySongs.get(position).toString());
                try {
                    mp.setDataSource(getApplicationContext(), u);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
                sb.setMax(mp.getDuration());
                updateSeekBar.start();
                break;
            case R.id.like:
                break;
            case R.id.dislike:
                mySongs.remove(mySongs.get(position));
                Toast.makeText(Player.this,mySongs.get(position).toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
