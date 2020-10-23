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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewDetail> {

    Context mContext;
    ArrayList<MusicFiles> album;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> album) {
        this.mContext = mContext;
        this.album = album;
    }

    @NonNull
    @Override
    public ViewDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.album_items,parent,false);
        return new ViewDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDetail holder, final int position) {

        holder.albumName.setText(album.get(position).getAlbum());
        byte[] image = getAlbum(album.get(position).getPath());

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
                Intent intent = new Intent(mContext,AlbumDetails.class);
                intent.putExtra("Album", album.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return album.size();
    }

    class ViewDetail extends RecyclerView.ViewHolder{

        ImageView album;
        TextView albumName;

        public ViewDetail(@NonNull View itemView) {
            super(itemView);

            album = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
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
