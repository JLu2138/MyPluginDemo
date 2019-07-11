package com.example.lujun858.myplugindemo;

public class JniUtils {
    static {
        System.loadLibrary("hello");
    }

    public static native String getString();
}
