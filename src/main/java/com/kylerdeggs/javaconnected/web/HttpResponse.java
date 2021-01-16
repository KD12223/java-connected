package com.kylerdeggs.javaconnected.web;

import java.time.LocalDateTime;

public class HttpResponse {
    private final LocalDateTime timeStamp;

    private final String status, message;

    public HttpResponse(String status, String message) {
        timeStamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
