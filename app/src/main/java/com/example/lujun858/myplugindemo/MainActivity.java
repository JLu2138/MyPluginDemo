package com.example.lujun858.myplugindemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView jumpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jumpBtn = findViewById(R.id.btn_jump2_plugin_activity);
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, MainActivity.class));
                Intent t = new Intent();
                t.setComponent(
                        new ComponentName("com.example.plugin1",
                                "com.example.plugin1.MainActivity"));

                startActivity(t);
            }
        });
    }
}
