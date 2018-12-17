package com.example.lujun858.myplugindemo;

import android.app.Application;
import android.content.Context;

import com.example.lujun858.myplugindemo.ams_hook.AMSHookHelper;

import java.util.HashMap;

public class HostApplication extends Application {


    private static Context sContext;
    private String apkName = "plugin1.apk";    //apk名称

    // 插件 Activity 与宿主中的占位 Activity 映射关系集合
    public static HashMap<String, String> pluginActiviesMap = new HashMap<>();



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = base;

        try {
            /*//模拟从服务器下载，把Assets里面得文件复制到 /data/data/files 目录下
            Utils.extractAssets(base, apkName);

            //合并宿主和插件的 dexElements 数组，并重设到宿主的 BaseDexClassLoader 中
            File dexFile = getFileStreamPath(apkName);
            File optDexFile = getFileStreamPath("plugin1.dex");
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);*/

            PluginManager.init(this);

            //get json data from server
            mockData();

            //hook AMN
            AMSHookHelper.hookAMN();
            //hook ActivityThread
            AMSHookHelper.hookActivityThread();


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    void mockData() {
        pluginActiviesMap.put("com.example.plugin1.MainActivity", "com.example.lujun858.myplugindemo.stub.SingleTaskActivity1");
    }

    public static Context getContext() {
        return sContext;
    }
}
