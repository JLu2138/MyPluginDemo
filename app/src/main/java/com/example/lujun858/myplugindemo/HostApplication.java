package com.example.lujun858.myplugindemo;

import android.app.Application;
import android.content.Context;

import com.example.lujun858.myplugindemo.ams_hook.AMSHookHelper;

import java.io.File;

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

            //合并宿主和插件的 dexElements 数组，并重设到宿主的 BaseDexClassLoader 中
            File dexFile = getFileStreamPath(apkName);
            File optDexFile = getFileStreamPath("plugin1.dex");
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);

            //hook AMN
            AMSHookHelper.hookAMN();
            //hook ActivityThread
            AMSHookHelper.hookActivityThread();


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return sContext;
    }
}
