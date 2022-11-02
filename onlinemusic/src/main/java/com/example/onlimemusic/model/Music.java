package com.example.onlimemusic.model;


import lombok.Data;

@Data
public class Music {
    private int id;

    private String title;

    private String singer;

    private String time;

    private String url;

    private int userid;
}
