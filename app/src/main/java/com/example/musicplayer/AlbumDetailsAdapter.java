package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewDetailed> {

    Context mContext;
    static ArrayList<MusicFiles> albums;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albums) {
        this.mContext = mContext;
        this.albums = albums;
    }

    @NonNull
    @Override
    public ViewDetailed onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.album_adapter_details,parent,false);
        return new ViewDetailed(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDetailed holder, final int position) {

        holder.albumName.setText(albums.get(position).getTitle());
        byte[] image = getAlbum(albums.get(position).getPath());

        if(image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album);
        }
        else
        {
            Glide.with(mContext).asBitmap().load(R.drawable.image).into(holder.album);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,SongPlayer.class);
                intent.putExtra("Sender" ,"Albums");
                intent.putExtra("position",position);
                mContext.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class ViewDetailed extends RecyclerView.ViewHolder{

        ImageView album;
        TextView albumName;

        public ViewDetailed(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.image1);
            albumName = itemView.findViewById(R.id.song_Name1);
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
