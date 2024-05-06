package com.example.electronic_magazine;

public class Grade {

    private String mark;
    private long timestamp;

    public Grade() {}

    public Grade(String mark, long timestamp) {
        this.mark = mark;
        this.timestamp = timestamp;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
