package com.rimir.linky.util;

public class URLSUtils {

    public static String getExtension(String url) {
        try {
            String fileExtension = url.substring(url.lastIndexOf("."));
            return fileExtension;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
