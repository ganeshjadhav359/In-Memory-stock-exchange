package org.ganeshjadhav.utils;

import lombok.Getter;

import java.util.UUID;

public class Common {
    private Common(){

    }
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    public static long getDefaultTTl(){
        return  10000;
    }

}
