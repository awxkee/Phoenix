package com.github.dozzatq.phoenix.CloudMessaging;

import java.util.Map;

public class FirebaseData {
    private Map<String, Object> data;
    private String collapseKey;
    private long timeTolive;
    private String to;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

   /* public Map<String, Object> getNotification() {
        return notification;
    }

    public void setNotification(Map<String, Object> notification) {
        this.notification = notification;
    }*/

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public long getTimeTolive() {
        return timeTolive;
    }

    public void setTimeTolive(long timeTolive) {
        this.timeTolive = timeTolive;
    }
}
