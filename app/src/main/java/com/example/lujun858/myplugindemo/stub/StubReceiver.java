package com.example.lujun858.myplugindemo.stub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lujun858.myplugindemo.ReceiverManager;

public class StubReceiver extends BroadcastReceiver {
    public StubReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String oldAction = intent.getAction();
        if(ReceiverManager.pluginReceiverMappings.containsKey(oldAction)) {
            String newAction = ReceiverManager.pluginReceiverMappings.get(oldAction);
            context.sendBroadcast(new Intent(newAction));
        }
    }
}
