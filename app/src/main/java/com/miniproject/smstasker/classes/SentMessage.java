package com.miniproject.smstasker.classes;

public class SentMessage {
    private String id, name, number, message, dateTime;

    public SentMessage() {
    }

    public SentMessage(String id, String name, String number, String message, String dateTime) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
