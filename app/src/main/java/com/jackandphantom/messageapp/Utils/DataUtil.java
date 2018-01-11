package com.jackandphantom.messageapp.Utils;



public class DataUtil {

    private static String userId;
    private static String image;

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        DataUtil.userId = userId;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        DataUtil.image = image;
    }
}
