package com.example.assignment2;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText; // For text messages, this field stores the text message. For audio messages, it stores the audio URL. For image messages, it stores the image URL.
    private String messageType; // Added field to indicate message type: "text", "audio", or "image"
    private long timestamp;

    private String imageUrl; // URL for the image, applicable only if messageType is "image"


    // Empty constructor for Firebase
    public Message() {
    }

    // Constructor with parameters for different types of messages
    public Message(String senderId, String receiverId, String messageText, String messageType, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    // Getter methods
    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String toString) {
    }
}