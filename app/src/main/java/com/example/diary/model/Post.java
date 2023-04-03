package com.example.diary.model;

import com.google.type.DateTime;
import com.vdurmont.emoji.Emoji;

public class Post {
    private String id;
    private String title;
    private String content;
    private String color;
    private String datetime;
    private String emoji;


    public Post() {
    }

    public Post(String id, String title, String content, String color, String datetime, String emoji) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.datetime = datetime;
        this.emoji = emoji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDateTime() {
        return datetime;
    }

    public void setDateTime(String datetime) {
        this.datetime = datetime;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
