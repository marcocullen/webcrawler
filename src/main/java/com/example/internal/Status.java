package com.example.internal;

public class Status {
    private final boolean isDone;
    private final String location;

    public Status(boolean isDone, String location) {
        this.isDone = isDone;
        this.location = location;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getLocation() {
        return location;
    }
}
