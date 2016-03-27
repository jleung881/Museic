package com.example.justin.museic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;

import org.w3c.dom.Text;

import java.util.ArrayList;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    String[] items;
    Button sync;
    TextView connect;

    public BluetoothAdapter mBluetoothAdapter;
    public MainActivity me;

    private DeviceListener listener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            connect = (TextView) findViewById(R.id.connect);
            connect.setTextColor(Color.GREEN);
            myo.unlock(Myo.UnlockType.HOLD);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lvPlaylist);
        sync = (Button) findViewById(R.id.sync);
        me = this;
        final Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())){
            Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        hub.setMyoAttachAllowance(1);
        hub.addListener(listener);

        sync.setOnClickListener(new ButtonListener(){
            @Override
            public void onClick(View v){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(!mBluetoothAdapter.isEnabled()){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,1);
                }
                hub.attachToAdjacentMyo();

            }
        });


        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for(int i = 0; i<mySongs.size(); i++){
            //toast(mySongs.get(i).getName().toString());
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(),R.layout.song_layout,R.id.textView,items);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",mySongs));
            };
        });
    };

    public ArrayList<File> findSongs(File root){
        ArrayList<File> a1 = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFiles : files){
          if(singleFiles.isDirectory() && !singleFiles.isHidden()){
            a1.addAll(findSongs(singleFiles));
          }
          else {
            if(singleFiles.getName().endsWith(".mp3") || singleFiles.getName().endsWith(".wav")){
                a1.add(singleFiles);
            };
          };
        };
        return a1;
    };

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
abstract class ButtonListener implements View.OnClickListener{

    public ButtonListener(){

    }

    @Override
    public abstract void onClick(View v);

}
