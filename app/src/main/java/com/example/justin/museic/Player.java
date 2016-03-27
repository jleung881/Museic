package com.example.justin.museic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener{
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    Uri u;
    Thread updateSeekBar;

    SeekBar sb;
    Button pause, ff, next, fb, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        pause = (Button) findViewById(R.id.pause);
        ff = (Button) findViewById(R.id.ff);
        next = (Button) findViewById(R.id.next);
        fb = (Button) findViewById(R.id.fb);
        previous = (Button) findViewById(R.id.previous);

        pause.setOnClickListener(this);
        ff.setOnClickListener(this);
        next.setOnClickListener(this);
        fb.setOnClickListener(this);
        previous.setOnClickListener(this);

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
        position = b.getInt("pos",0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
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

    }

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
                break;
        }
    }
}
