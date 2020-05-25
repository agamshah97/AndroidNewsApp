package com.example.mywebapp;

import java.time.LocalDateTime;


public class Newscard
{
    private String title;
    private String image_url;
    private String section;
    private String time;
    private String url;
    private String id;

    public Newscard(String title, String image_url, String section, String time, String url, String id) {
        this.title = title;
        this.image_url = image_url;
        this.section = section;
        this.time = time;
        this.url = url;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Newscard{" +
                "title='" + title + '\'' +
                ", image_url='" + image_url + '\'' +
                ", section='" + section + '\'' +
                ", time='" + time + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image_url;
    }

    public String getSection() {
        return section;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }


    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image_url) {
        this.image_url = image_url;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }
}
