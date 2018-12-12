package com.example.lujun858.myplugindemo;

import android.app.Application;
import android.content.Context;

public class HostApplication extends Application {


    private static Context sContext;
    private String apkName = "plugin1.apk";    //apk名称


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;

        try {
            //模拟从服务器下载，把Assets里面得文件复制到 /data/data/files 目录下
            Utils.extractAssets(base, apkName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return sContext;
    }
}
