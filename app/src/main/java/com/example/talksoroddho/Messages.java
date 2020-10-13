package com.example.talksoroddho;


import java.util.Map;

public class Messages
{
    private String from, message, type;
    private Map<String, String> timestamp;



    public Messages() {
    }

    public Messages(String from, String message, String type, Map<String, String> timestamp) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
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

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}
