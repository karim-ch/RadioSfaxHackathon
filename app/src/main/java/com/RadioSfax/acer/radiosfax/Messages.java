package com.RadioSfax.acer.radiosfax;


public class Messages {


    private String message, type, image, name;
    private long time;
    private boolean seen;
    private String from;

    public Messages() {
    }

    public Messages(String message, String type, long time, boolean seen, String from, String name, String image) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
        this.image = image;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}