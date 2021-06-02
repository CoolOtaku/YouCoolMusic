package com.example.youcoolmusic;

public class Video {
    public String id;
    public String title;
    public String img;

    public Video(String id, String title, String img) {
        this.id = id;
        this.title = title;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
