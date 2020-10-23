package com.example.musicplayer;

public class MusicFiles {

    private String artsit;
    private String path;
    private String album;
    private String duration;
    private String title;
    private String id;

    public MusicFiles(String artsit, String path, String album, String duration, String title, String id) {
        this.artsit = artsit;
        this.path = path;
        this.album = album;
        this.duration = duration;
        this.title = title;
        this.id = id;
    }

    public String getArtsit() {
        return artsit;
    }

    public String getPath() {
        return path;
    }

    public String getAlbum() {
        return album;
    }

    public String getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getId() { return id; }
}
