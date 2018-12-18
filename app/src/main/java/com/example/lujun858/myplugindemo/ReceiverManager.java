package com.example.lujun858.myplugindemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.mypluginlibrary.RefInvoke;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ReceiverManager {

    public static HashMap<String, String> pluginReceiverMappings = new HashMap<>();

    private static final String TAG = "ReceiverManager";

    /**
     * 解析插件Apk文件中的 <receiver>, 并存储起来
     *
     * @param apkFile
     * @throws Exception
     */
    public static void preLoadReceiver(Context context, File apkFile) {
        // 首先调用parsePackage获取到apk对象对应的Package对象
        Object packageParser = RefInvoke.createObject("android.content.pm.PackageParser");
        Class[] p1 = {File.class, int.class};
        Object[] v1 = {apkFile, PackageManager.GET_RECEIVERS};
        Object packageObj = RefInvoke.invokeInstanceMethod(packageParser, "parsePackage", p1, v1);

        String packageName = (String)RefInvoke.getFieldObject(packageObj, "packageName");

        // 读取Package对象里面的receivers字段,注意这是一个 List<PackageParser.Activity> (没错,底层把<receiver>当作<activity>处理)
        // 接下来要做的就是根据这个List<Activity> 获取到Receiver对应的 ActivityInfo (依然是把receiver信息用activity处理了)
        List receivers = (List) RefInvoke.getFieldObject(packageObj, "receivers");

        try {
            for (Object receiver : receivers) {
                Bundle metadata = (Bundle)RefInvoke.getFieldObject(
                        "android.content.pm.PackageParser$Component", receiver, "metaData");
                String oldAction = metadata.getString("oldAction");

                // 解析出 receiver以及对应的 intentFilter
                List<? extends IntentFilter> filters = (List<? extends IntentFilter>) RefInvoke.getFieldObject(
                        "android.content.pm.PackageParser$Component", receiver, "intents");

                // 把解析出来的每一个静态Receiver都注册为动态的
                for (IntentFilter intentFilter : filters) {
                    ActivityInfo receiverInfo = (ActivityInfo) RefInvoke.getFieldObject(receiver, "info");
                    BroadcastReceiver broadcastReceiver = (BroadcastReceiver) RefInvoke.createObject(receiverInfo.name);
                    context.registerReceiver(broadcastReceiver, intentFilter);

                    String newAction = intentFilter.getAction(0);
                    ReceiverManager.pluginReceiverMappings.put(oldAction, newAction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
