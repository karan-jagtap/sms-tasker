package com.miniproject.smstasker.classes;

public class TaskClass {
    private String timerName, fromTimeData, toTimeData, messageData;

    public TaskClass() {
        fromTimeData = "";
        toTimeData = "";
        messageData = "";
    }

    public TaskClass(String timerName, String fromTimeData, String toTimeData, String messageData) {
        this.timerName = timerName;
        this.fromTimeData = fromTimeData;
        this.toTimeData = toTimeData;
        this.messageData = messageData;
    }

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public String getFromTimeData() {
        return fromTimeData;
    }

    public void setFromTimeData(String fromTimeData) {
        this.fromTimeData = fromTimeData;
    }

    public String getToTimeData() {
        return toTimeData;
    }

    public void setToTimeData(String toTimeData) {
        this.toTimeData = toTimeData;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public boolean isEmpty() {
        if (fromTimeData != null) {
            return false;
        }
        if (toTimeData != null) {
            return false;
        }
        if (messageData != null) {
            return false;
        }
        return true;
    }
}
