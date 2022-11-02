package com.example.onlimemusic.tools;


import lombok.Data;

@Data
public class ResponseBodyMessage<T> {
    private int status; //状态码

    private String message; //状态描述信息

    private T data;//返回给前端的数据

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
