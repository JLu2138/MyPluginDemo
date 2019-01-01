package com.example.lujun858.myplugindemo.ams_hook;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.example.lujun858.myplugindemo.HostApplication;
import com.example.lujun858.myplugindemo.ServiceManager;
import com.example.lujun858.myplugindemo.stub.ProxyService;
import com.example.lujun858.myplugindemo.stub.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


class MockClass1 implements InvocationHandler {

    private static final String TAG = "MockClass1";

    // 替身StubActivity的包名, 也就是我们自己的包名
    private static final String STUB_ACTIVITY_PACKAGENAME = "com.example.lujun858.myplugindemo";

    Object mBase;

    public MockClass1(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Log.e("JLu", method.getName());

        if ("startActivity".equals(method.getName())) {
            // 只拦截这个方法
            // 替换参数, 任你所为;甚至替换原始Activity启动别的Activity偷梁换柱

            // 找到参数里面的第一个Intent 对象
            Intent raw;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            raw = (Intent) args[index];

            Intent newIntent = new Intent();


            // 这里我们把启动的Activity临时替换为 StubActivity, 查询映射表找到插件 Activity 对应的 LaunchModeActivity，没有就默认使用 StubActivity
            ComponentName componentName;

            String rawClass = raw.getComponent().getClassName();
            if(HostApplication.pluginActiviesMap.containsKey(rawClass)) {
                String activity = HostApplication.pluginActiviesMap.get(rawClass);
                componentName = new ComponentName(STUB_ACTIVITY_PACKAGENAME, activity);
            } else {
                componentName = new ComponentName(STUB_ACTIVITY_PACKAGENAME, StubActivity.class.getName());
            }
            newIntent.setComponent(componentName);




            // 把我们原始要启动的TargetActivity先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, raw);

            // 替换掉Intent, 达到欺骗AMS的目的
            args[index] = newIntent;

            Log.d(TAG, "hook success");
            return method.invoke(mBase, args);

        } else if ("startService".equals(method.getName())) {
            /**
             * 替换要启动的插件 Service 信息为 StubService信息
             */

            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            //get ProxyService form Application.pluginServices
            Intent rawIntent = (Intent) args[index];

            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = HostApplication.getContext().getPackageName();


            // replace Plugin Service of ProxyService
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            Intent newIntent = new Intent();
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetService先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, rawIntent);

            // Replace Intent, cheat AMS
            args[index] = newIntent;

            Log.d(TAG, "hook success");
            return method.invoke(mBase, args);

        } else if ("stopService".equals(method.getName())) {
            // 只拦截这个方法
            // 替换参数, 任你所为;甚至替换原始ProxyService启动别的Service偷梁换柱

            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            Intent rawIntent = (Intent) args[index];
            Log.d(TAG, "hook success");
            return ServiceManager.getInstance().stopService(rawIntent);
        } else if("bindService".equals(method.getName())) {
            // 只拦截这个方法
            // 替换参数, 任你所为;甚至替换原始ProxyService启动别的Service偷梁换柱

            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            //get ProxyService form UPFApplication.pluginServices
            Intent rawIntent = (Intent) args[index];

            //stroe intent-conn
            ServiceManager.getInstance().mServiceMap2.put(args[4], rawIntent);

            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = HostApplication.getContext().getPackageName();

            // replace Plugin Service of ProxyService
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            Intent newIntent = new Intent();
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetService先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, rawIntent);

            // Replace Intent, cheat AMS
            args[index] = newIntent;

            Log.d(TAG, "hook success");
            return method.invoke(mBase, args);

        } else if("unbindService".equals(method.getName())) {
            //以 conn 为 key 得到绑定的 Intent，ServiceManager通过 intent找到对应的 Service 解绑
            Intent rawIntent = ServiceManager.getInstance().mServiceMap2.get(args[0]);
            ServiceManager.getInstance().onUnbind(rawIntent);
            return method.invoke(mBase, args);
        }

        return method.invoke(mBase, args);
    }
}