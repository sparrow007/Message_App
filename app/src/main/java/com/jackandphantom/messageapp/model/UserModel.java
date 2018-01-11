package com.jackandphantom.messageapp.model;


public class UserModel  {

    private String userName;
    private String createAt;
    private String image=null;
    private String noOfMessages="0";

    public UserModel() {
    }

    public UserModel(String userName, String createAt, String image, String noOfMessages) {
        this.userName = userName;
        this.createAt = createAt;
        this.image = image;
        this.noOfMessages = noOfMessages;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNoOfMessages() {
        return noOfMessages;
    }

    public void setNoOfMessages(String noOfMessages) {
        this.noOfMessages = noOfMessages;
    }
}
