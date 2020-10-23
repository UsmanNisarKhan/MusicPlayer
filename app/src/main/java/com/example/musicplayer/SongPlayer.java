package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.musicplayer.AlbumDetailsAdapter.albums;
import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.MusicAdapter.music;

public class SongPlayer extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private TextView songName, artistName, durationStart, durationEnd;
    private ImageView back, menu, previous, next, shuffle, repeat, songImage,gradientImage;
    private FloatingActionButton fab;
    private SeekBar seekBar;
    int position = 0;
    ArrayList<MusicFiles> list = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    Uri uri;
    Timer timer;
    int toggle = 0;
    boolean shufflebtn, repeatbtn;
    Bitmap bitmap;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        init();

        getIntentExtra();
        songName.setText(list.get(position).getTitle());
        artistName.setText(list.get(position).getArtsit());
        mediaPlayer.setOnCompletionListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mediaPlayer.pause();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                    fab.setImageResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();

            }
        });

        SongPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null) {

                    int total = mediaPlayer.getDuration() / 1000;
                    durationEnd.setText(formattedTime(total));

                    int current = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(current);
                    durationStart.setText(formattedTime(current));
                }
                handler.postDelayed(this, 1000);

            }
        });
    }

    private String formattedTime(int current) {

        String start = "";
        int seconds = (current % 60);
        int minutes = (current / 60) % 60;
        int hours = (current / 3600);

        if (hours > 0) {
            start = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            start = String.format("%02d:%02d", minutes, seconds);
        }

        return start;
    }

    private void getIntentExtra() {

        position = getIntent().getIntExtra("position", 0);
        String sender = getIntent().getStringExtra("Sender");

        if(sender != null && sender.equals("Albums"))
        {
            list = albums;
        }
        else {
            list = music;
        }

        if (list != null) {
            fab.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(list.get(position).getPath());
        }

        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);

    }

    private void init() {

        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.artist_name);
        durationStart = findViewById(R.id.duration_start);
        durationEnd = findViewById(R.id.duration_end);

        songImage = findViewById(R.id.song_image);
        gradientImage = findViewById(R.id.gradient_image);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        shuffle = findViewById(R.id.shuffle);
        shuffle.setOnClickListener(this);

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(this);

        next = findViewById(R.id.next);
        next.setOnClickListener(this);

        previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);

        repeat = findViewById(R.id.repeat);
        repeat.setOnClickListener(this);

        fab = findViewById(R.id.play_pause);
        fab.setOnClickListener(this);

        seekBar = findViewById(R.id.seekbar);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_pause:

                playPause();

                break;

            case R.id.next:

                next();

                break;

            case R.id.previous:

                previous();

                break;

            case R.id.shuffle:

                shuffle();

                break;

            case R.id.repeat:

                repeat();

                break;

            case R.id.back:

                backtoMain();

                break;
        }
    }

    private void metaData(Uri uri) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());
        byte[] pic = mediaMetadataRetriever.getEmbeddedPicture();

        if (pic != null) {

           // Glide.with(this).asBitmap().load(pic).into(songImage);                                 For implementing Images without animation.
            bitmap = BitmapFactory.decodeByteArray(pic,0,pic.length);
            animation(bitmap,songImage);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {

                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch != null)
                    {
                        ImageView gradient = findViewById(R.id.gradient_image);
                        LinearLayout layout = findViewById(R.id.linear);
                        gradient.setBackgroundResource(R.drawable.song_image_bg);
                        layout.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[] {swatch.getRgb() , 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[] {swatch.getRgb() , swatch.getRgb()});
                        layout.setBackground(gradientDrawableBg);

                        songName.setTextColor(swatch.getTitleTextColor());
                        artistName.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        ImageView gradient = findViewById(R.id.gradient_image);
                        LinearLayout layout = findViewById(R.id.linear);
                        gradient.setBackgroundResource(R.drawable.song_image_bg);
                        layout.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[] {0xff000000 , 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[] {0xff000000 , 0xff000000});
                        layout.setBackground(gradientDrawableBg);

                        songName.setTextColor(Color.WHITE);
                        artistName.setTextColor(Color.WHITE);
                    }

                }
            });

        } else {

            animationDefault(songImage);

            ImageView gradient = findViewById(R.id.song_image);
            LinearLayout layout = findViewById(R.id.linear);
            gradient.setBackgroundResource(R.drawable.song_image_bg);
            layout.setBackgroundResource(R.drawable.main_bg);

            songName.setTextColor(Color.WHITE);
            artistName.setTextColor(Color.WHITE);
        }

    }


    void playPause() {

        if (toggle == 0) {

            fab.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);

                }
            }, 0, 1000);


            mediaPlayer.start();

            toggle = 1;

        } else {
            fab.setImageResource(R.drawable.ic_baseline_play_arrow_24);

            mediaPlayer.pause();
            timer.cancel();

            toggle = 0;
        }
    }

    void next() {

        repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
        shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);

        fab.setImageResource(R.drawable.ic_baseline_pause_24);

            mediaPlayer.stop();
            mediaPlayer.release();
            position = position + 1 % list.size();
            uri = Uri.parse(list.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);

            songName.setText(list.get(position).getTitle());
            artistName.setText(list.get(position).getArtsit());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);

        }

    void previous() {

        repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
        shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);

        fab.setImageResource(R.drawable.ic_baseline_pause_24);

        if(position == 0)
        {
            position = list.size() - 1 ;
            uri = Uri.parse(list.get(position).getPath());

        }

        mediaPlayer.stop();
        mediaPlayer.release();
        position = position - 1 % list.size();
        uri = Uri.parse(list.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);

        songName.setText(list.get(position).getTitle());
        artistName.setText(list.get(position).getArtsit());

        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);

    }

    public void animation(final Bitmap bitmap, final ImageView imageView)
    {
        final Animation animin = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
        Animation animout = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);

        animout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(getApplicationContext()).load(bitmap).into(imageView);
                animin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });imageView.startAnimation(animout);

    }

    public void animationDefault(final ImageView imageView)
    {
        final Animation animin = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
        Animation animout = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);

        animout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(getApplicationContext()).load(R.drawable.image).into(imageView);
                animin.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });imageView.startAnimation(animout);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {


        if(repeatbtn == true && shufflebtn == false)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            uri = Uri.parse(list.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);

            songName.setText(list.get(position).getTitle());
            artistName.setText(list.get(position).getArtsit());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
        else if(shufflebtn ==  true && repeatbtn == false)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = getrandom(list.size() -1);
            uri = Uri.parse(list.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);

            songName.setText(list.get(position).getTitle());
            artistName.setText(list.get(position).getArtsit());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }

        else {

            next();

            repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
            shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }

    }

    private int getrandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    public void shuffle()
    {
        if(shufflebtn == false) {
            shuffle.setImageResource(R.drawable.ic_baseline_shuffle_on);
            shufflebtn = true;
            ShuffleToast(this,"Shuffle On");
        }
        else
        {
            shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);
            shufflebtn = false;
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    public void repeat()
    {
        if(repeatbtn == false) {
            repeat.setImageResource(R.drawable.ic_baseline_repeat_on);
            repeatbtn = true;
            RepeatToast(this,"Repeat On");
        }
        else
        {
            repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
            repeatbtn = false;
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    public void ShuffleToast(Context context,String msg)
    {
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.LEFT,30,60);
        toast.show();
    }

    public void RepeatToast(Context context,String msg)
    {
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT,30,60);
        toast.show();
    }

    public void backtoMain()
    {
        Intent intent = new Intent(SongPlayer.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}