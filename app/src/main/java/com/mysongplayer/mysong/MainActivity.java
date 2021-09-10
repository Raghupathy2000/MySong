package com.mysongplayer.mysong;

import  androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewSong);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            runtimePermission();
        else
            displaySong();
    }

    public void runtimePermission() {
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            displaySong();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();

    }
    public ArrayList<File> findSong (File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File singlefile: files)
        {
            if (singlefile.isDirectory() && !singlefile.isHidden())
            {
                arrayList.addAll(findSong(singlefile));
            }
            else
            {
                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav"))
                {
                    arrayList.add(singlefile);
                }
            }
        }
        return  arrayList;
    }

    public void  displaySong()
    {
        final  ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i=0; i<mySongs.size(); i++)
        {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3"," ").replace(".wav"," ");
        }
       customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = mySongs.get(position).toString();

                Bundle b1= new Bundle();
                b1.putSerializable("AllSongs",mySongs);
                b1.putInt("pos",position);
                Intent i = new Intent(getApplicationContext(),PlayerActivity.class);
                i.putExtras(b1);
//                Toast.makeText(MainActivity.this, position+songName+mySongs.size(), Toast.LENGTH_LONG).show();
                startActivity(i);


//                //*******************************************************************
//                Toast.makeText(MainActivity.this, position+songName+mySongs.size(), Toast.LENGTH_LONG).show();
            }
        });
    }

    class  customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView txtSong = view.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(items[position]);
            return view;
        }
    }

}
