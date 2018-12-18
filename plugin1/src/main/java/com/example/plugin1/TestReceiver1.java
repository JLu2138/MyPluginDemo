package com.example.plugin1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestReceiver1 extends BroadcastReceiver {
    public TestReceiver1() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,
                "TestReceiver1 接收到 host 广播 action=" + intent.getAction() + "  msg=" +intent.getStringExtra("msg"),
                Toast.LENGTH_LONG).show();
        context.sendBroadcast(new Intent("com.plugin1.action2"));
    }
}
