package com.example.plugin1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.mypluginlibrary.IMyInterface;


public class TestService2 extends Service {
    private int count;

    private MyBinder binder = new MyBinder();
    public class MyBinder extends Binder implements IMyInterface {

        @Override
        public int getCount() {
            return count;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("JLu", "Service is binded");

        count = count + 1;
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count = count + 1;
        Log.e("JLu", "Service is created");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        count = count + 1;
        Log.e("JLu", "Service is Unbind");
        Toast.makeText(getApplicationContext(), "TestService2 is onUnbind", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void onDestroy() {
        count = count + 1;
        super.onDestroy();

        Log.e("JLu", "Service is Destroy");
    }
}