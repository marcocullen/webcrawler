package com.example.dto;

public class StatusDTO {
    boolean complete;
    String location;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public StatusDTO(boolean complete, String location) {
        this.complete = complete;
        this.location = location;
    }
}
