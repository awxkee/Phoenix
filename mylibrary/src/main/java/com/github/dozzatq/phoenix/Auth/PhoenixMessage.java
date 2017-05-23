package com.github.dozzatq.phoenix.Auth;

/**
 * Created by dxfb on 21.05.2017.
 */

public class PhoenixMessage {
    private String key;
    private String ownerId;
    private Boolean ownerSeen;
    private String messageBody;
    private String[] recipientId;
    private long messageTime;
    private String roomId;

    public PhoenixMessage()
    {
        messageTime = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getOwnerSeen() {
        return ownerSeen;
    }

    public void setOwnerSeen(Boolean ownerSeen) {
        this.ownerSeen = ownerSeen;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String[] getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String[] recipientId) {
        this.recipientId = recipientId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
