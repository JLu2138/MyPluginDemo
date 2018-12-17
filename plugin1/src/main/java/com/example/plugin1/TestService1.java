package com.example.plugin1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TestService1 extends Service {
    public TestService1() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //在反射调用 handleCreateService 方法创建 Service 时时被调用
        Log.e("JLu", "Service is created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("JLu", "Service is started");
        Toast.makeText(getApplicationContext(), "TestService1 is started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("JLu", "Service is Destroy");
        Toast.makeText(getApplicationContext(), "TestService1 is Destroy", Toast.LENGTH_SHORT).show();

    }
}
