package com.collacode.document.component;


import java.util.UUID;

public class IdGenerator {
    public static String genId(){
        return UUID.randomUUID().toString();
    }
}
