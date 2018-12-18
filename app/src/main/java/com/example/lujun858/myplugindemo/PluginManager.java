package com.example.lujun858.myplugindemo;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.example.mypluginlibrary.RefInvoke;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    //插件信息的集合
    public final static List<PluginItem> plugins = new ArrayList<>();

    //正在使用的Resources，作为全局新 Resource 使用
    public static volatile Resources mNowResources;

    //原始的application中的BaseContext，不能是其他的，否则会内存泄漏
    public static volatile Context mBaseContext;

    //ContextImpl 中的 LoadedAPK 对象 mPackageInfo
    private static Object mPackageInfo = null;

    /**
     * 初始化一些成员变量和加载已安装的插件
     * @param application
     */
    public static void init(Application application) {

        mBaseContext = application.getBaseContext();
        mPackageInfo = RefInvoke.getFieldObject(mBaseContext, "mPackageInfo");
        mNowResources = mBaseContext.getResources();

        try {
            //遍历 assets 目录，找到插件 apk
            AssetManager assetManager = application.getAssets();
            String[] paths = assetManager.list("");

            ArrayList<String> pluginPaths = new ArrayList<String>();
            for (String path : paths) {
                if (path.endsWith(".apk")) {

                    String apkName = path;
                    String dexName = apkName.replace(".apk", ".dex");

                    //模拟下载到本地
                    Utils.extractAssets(mBaseContext, apkName);

                    //合并宿主和插件 dex
                    mergeDexs(apkName, dexName);

                    File apkFile = mBaseContext.getFileStreamPath(apkName);

                    //解析插件中的Service组件
                    ServiceManager.getInstance().preLoadServices(apkFile);

                    //解析插件 AndroidManifest 中注册的静态广播
                    ReceiverManager.preLoadReceiver(mBaseContext, apkFile);

                    //安装插件中的Providers
                    ProviderHelper.installProviders(mBaseContext, apkFile);

                    //提取插件信息存储
                    PluginItem item = generatePluginItem(apkName);
                    plugins.add(item);

                    //得到所有插件路径
                    pluginPaths.add(item.pluginPath);
                }
            }

            // 合并宿主和所有插件的资源
            reloadInstalledPluginResources(pluginPaths);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取插件 apk 的信息，用 PluginItem 实例保存
     * @param apkName
     * @return
     */
    private static PluginItem generatePluginItem(String apkName) {
        File file = mBaseContext.getFileStreamPath(apkName);
        PluginItem item = new PluginItem();
        item.pluginPath = file.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(mBaseContext, item.pluginPath);

        return item;
    }

    /**
     * 合并宿主和插件的 dexElements 数组，并重设到宿主的 BaseDexClassLoader 中
     * todo 收集完所有插件的 apk 路径后再一次性替换，提高效率
     * @param apkName
     * @param dexName
     */
    static void mergeDexs(String apkName, String dexName) {

        File dexFile = mBaseContext.getFileStreamPath(apkName);
        File optDexFile = mBaseContext.getFileStreamPath(dexName);

        try {
            BaseDexClassLoaderHookHelper.patchClassLoader(mBaseContext.getClassLoader(), dexFile, optDexFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 合并宿主和插件的资源生成"超级"AssetManager 和 "超级" Resources，并重新设给 mBaseContext 和 mBaseContext.mPackageInfo
     * @param pluginPaths 插件路径集合
     */
    private static void reloadInstalledPluginResources(ArrayList<String> pluginPaths) {
        try {
            // 反射创建一个新的"超级" AssetManager，调用其 addAssetPath 方法，添加宿主和所有插件的资源
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);

            addAssetPath.invoke(assetManager, mBaseContext.getPackageResourcePath());

            for(String pluginPath: pluginPaths) {
                addAssetPath.invoke(assetManager, pluginPath);
            }


            //通过 "超级" AssetManager 生成"超级" Resources
            Resources newResources = new Resources(assetManager,
                    mBaseContext.getResources().getDisplayMetrics(),
                    mBaseContext.getResources().getConfiguration());



            RefInvoke.setFieldObject(mBaseContext, "mResources", newResources);
            //这是最主要的需要替换的，如果不支持插件运行时更新，只留这一个就可以了
            RefInvoke.setFieldObject(mPackageInfo, "mResources", newResources);

            mNowResources = newResources;

            //需要清理mTheme对象，否则通过inflate方式加载资源会报错
            //如果是activity动态加载插件，则需要把activity的mTheme对象也设置为null
            RefInvoke.setFieldObject(mBaseContext, "mTheme", null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
