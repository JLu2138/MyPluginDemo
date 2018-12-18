package com.example.plugin1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestReceiver2 extends BroadcastReceiver {
    static final String ACTION = "com.host.action";

    public TestReceiver2() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,
                "TestReceiver2 接收到 TestReceiver1 广播， intent=" + intent.getAction() + " msg="  + intent.getStringExtra("msg"),
                Toast.LENGTH_LONG).show();

        context.sendBroadcast(new Intent(ACTION));
    }
}
