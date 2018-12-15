package com.example.lujun858.myplugindemo.ams_hook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.mypluginlibrary.RefInvoke;

class MockClass2 implements Handler.Callback {

    Handler mBase;

    public MockClass2(Handler base) {
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            // ActivityThread里面 "LAUNCH_ACTIVITY" 这个字段的值是100
            // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
            case 100:
                handleLaunchActivity(msg);
                break;
        }

        mBase.handleMessage(msg);
        return true;
    }

    private void handleLaunchActivity(Message msg) {
        // 这里简单起见,直接取出TargetActivity; 应该先判断是否是启动插件 Activity

        Object obj = msg.obj;

        // 把替身恢复成真身
        Intent raw = (Intent) RefInvoke.getFieldObject(obj, "intent");

        Intent target = raw.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
        if (target != null) {
            //说明不是启动插件 Activity
            raw.setComponent(target.getComponent());
        }
    }


}
