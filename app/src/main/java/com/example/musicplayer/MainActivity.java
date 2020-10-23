package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    static ArrayList<MusicFiles> musicFiles;
    static ArrayList<MusicFiles> albumFiles = new ArrayList<>();
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validatePermission();

    }

    private void init()
    {
         viewPager= findViewById(R.id.viewpager);
         tabLayout = findViewById(R.id.tablayout);

        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addfragments(new SongsFragment(),"Songs");
        viewpagerAdapter.addfragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewpagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }


    public static class ViewpagerAdapter extends FragmentPagerAdapter{

        private ArrayList<String> title =  new ArrayList<>();
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);

        }

        public void addfragments(Fragment fragment,String titles)
        {
            fragments.add(fragment);
            title.add(titles);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }

    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 101)
        {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Permissions Granted",Toast.LENGTH_SHORT).show();
                musicFiles = getMusicFiles(getApplicationContext());
                init();

            }
            else
            {
                Toast.makeText(MainActivity.this,"Permissions Required",Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }

    public void validatePermission()
    {

        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermission();

        }else
            {
            musicFiles = getMusicFiles(getApplicationContext());
            init();
        }


    }

    public ArrayList<MusicFiles> getMusicFiles(Context context)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Sorting",MODE_PRIVATE);
        String sort = sharedPreferences.getString("sorting","name");
        ArrayList<String> duplicate = new ArrayList<>();
        albumFiles.clear();
        ArrayList<MusicFiles> list = new ArrayList<>();
        String order = null;
        Uri uri =  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sort)
        {
            case "name":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "date":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

            case "size":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }
        String[] projection ={

                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID
        };

        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,order);

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String artist = cursor.getString(0);
                String path = cursor.getString(1);
                String album = cursor.getString(2);
                String duration = cursor.getString(3);
                String title = cursor.getString(4);
                String id = cursor.getString(5);

                list.add(new MusicFiles(artist,path,album,duration,title,id));
                if(! duplicate.contains(album))
                {
                    albumFiles.add(new MusicFiles(artist,path,album,duration,title,id));
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        getMenuInflater().inflate(R.menu.sort,menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String input = newText.toLowerCase();
        ArrayList<MusicFiles> files = new ArrayList<>();
        for(MusicFiles song : musicFiles)
        {
            if(song.getTitle().toLowerCase().contains(input))
            {
                files.add(song);
            }
        }
        SongsFragment.musicAdapter.update(files);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor = getSharedPreferences("Sorting",MODE_PRIVATE).edit();

        switch (item.getItemId())
        {
            case R.id.name:

                editor.putString("sorting","name");
                editor.apply();
                this.recreate();

                break;

            case R.id.date:

                editor.putString("sorting","date");
                editor.apply();
                this.recreate();

                break;

            case R.id.size:

                editor.putString("sorting","size");
                editor.apply();
                this.recreate();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}