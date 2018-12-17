package com.example.lujun858.myplugindemo;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mypluginlibrary.IMyInterface;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button jumpBtn;
    private Button startServieBtn;
    private Button stopServiceBtn;
    private Button bindServiceBtn;
    private Button unbindServiceBtn;
    private ImageView imgView;

    private ServiceConnection conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jumpBtn = (Button) findViewById(R.id.btn_jump2_plugin_activity);
        jumpBtn.setOnClickListener(this);
        startServieBtn = (Button) findViewById(R.id.btn_start_service);
        startServieBtn.setOnClickListener(this);
        stopServiceBtn = (Button) findViewById(R.id.btn_stop_service);
        stopServiceBtn.setOnClickListener(this);
        bindServiceBtn = (Button) findViewById(R.id.btn_bind_service);
        bindServiceBtn.setOnClickListener(this);
        unbindServiceBtn = (Button) findViewById(R.id.btn_unbind_service);
        unbindServiceBtn.setOnClickListener(this);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("JLu", "onServiceConnected");

                IMyInterface a = (IMyInterface)service;
                int result = a.getCount();

                Log.d("JLu", String.valueOf(result));
                Toast.makeText(getApplicationContext(), "TestService2 is onBind，getResult= " + result, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("JLu", "onServiceDisconnected");
                Toast.makeText(getApplicationContext(), "TestService2 is Disconnected", Toast.LENGTH_SHORT).show();

            }
        };



      /*  imgView = (ImageView) findViewById(R.id.img);
        int imgId = 0;
        try {
            Class drawableClass = getClassLoader().loadClass("com.example.plugin1.R$drawable");
            imgId = (int) RefInvoke.getStaticFieldObject(drawableClass, "plugin");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        imgView.setImageDrawable(getApplicationContext().getDrawable(imgId));*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jump2_plugin_activity:
                //                startActivity(new Intent(MainActivity.this, MainActivity.class));
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.example.plugin1",
                                "com.example.plugin1.MainActivity"));

                startActivity(intent);
                break;
            case R.id.btn_start_service:
                intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.example.plugin1",
                                "com.example.plugin1.TestService1"));

                startService(intent);
                break;
            case R.id.btn_stop_service:
                intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.example.plugin1",
                                "com.example.plugin1.TestService1"));
                stopService(intent);
                break;
            case R.id.btn_bind_service:
                intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.example.plugin1",
                                "com.example.plugin1.TestService2"));
                bindService(intent, conn, Service.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                unbindService(conn);
                break;
            default:
                break;
        }
    }
}
