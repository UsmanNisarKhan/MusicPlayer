package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewDetails> {


    static ArrayList<MusicFiles> music;
    Context mContext;

    public MusicAdapter(ArrayList<MusicFiles> musicFiles, Context mContext) {
        this.music = musicFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.songs_items,parent,false);
        return new ViewDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDetails holder, final int position) {

        holder.textView.setText(music.get(position).getTitle());
        byte[] image = getImage(music.get(position).getPath());

        if(image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.imageView);
        }
        else
        {
            Glide.with(mContext).asBitmap().load(R.drawable.image).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,SongPlayer.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);


            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        delete(position,v);
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {

        return music.size();
    }


    public class ViewDetails extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView,menu;

        public ViewDetails(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.song_Name);
            imageView = itemView.findViewById(R.id.image);
            menu = itemView.findViewById(R.id.menu);
        }
    }

        private byte[] getImage(String uri) {

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(uri);
            byte[] pic = mediaMetadataRetriever.getEmbeddedPicture();
            mediaMetadataRetriever.release();
            return pic;
        }

        public void delete(int position, final View v)
        {
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Long.parseLong(music.get(position).getId()));

                File file = new File(music.get(position).getPath());
                boolean deleted = file.delete();
                if(deleted)
                {
                    mContext.getContentResolver().delete(uri,null,null);
                    music.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,music.size());
                    Snackbar.make(v,"File deleted", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    Snackbar.make(v,"File can't be deleted", Snackbar.LENGTH_SHORT)
                            .show();
                }
        }

        void update(ArrayList<MusicFiles> musicFilesArrayList)
        {
            music = new ArrayList<>();
            music.addAll(musicFilesArrayList);
            notifyDataSetChanged();
        }
}