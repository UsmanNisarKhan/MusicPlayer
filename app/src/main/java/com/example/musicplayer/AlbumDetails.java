package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.musicplayer.MainActivity.musicFiles;

public class AlbumDetails extends AppCompatActivity {
    
    ImageView albumImage;
    RecyclerView recyclerView;
    String album;
    private ArrayList<MusicFiles> albumlisT = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        recyclerView = findViewById(R.id.recyclerview);
        albumImage = findViewById(R.id.album_details_image);

        getIntentExtras();

        if(!(albumlisT.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this,albumlisT);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(albumDetailsAdapter);

        }

    }

    private void getIntentExtras() {

        album = getIntent().getStringExtra("Album");


        int j = 0;
        for(int i = 0 ; i < musicFiles.size() ; i++)
        {
            if(album.equals(musicFiles.get(i).getAlbum()))
            {
                albumlisT.add(j, musicFiles.get(i));
                j++;
            }
        }

        byte[] image = getAlbum(albumlisT.get(0).getPath());

        if(image != null)
        {
            Glide.with(this).load(image).into(albumImage);
        }
        else
        {
            Glide.with(this).load(R.drawable.image).into(albumImage);
        }
    }

    private byte[] getAlbum(String uri) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] pic = mediaMetadataRetriever.getEmbeddedPicture();
        mediaMetadataRetriever.release();
        return pic;
    }
}