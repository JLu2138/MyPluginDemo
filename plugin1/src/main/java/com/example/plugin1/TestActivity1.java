package com.example.plugin1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mypluginlibrary.BasePluginActivity;

public class TestActivity1 extends BasePluginActivity {
    private Button btnJump1;
    private Button btnJump2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_1);

        btnJump1 = (Button) findViewById(R.id.btn_jump1);
        btnJump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity1.this, MainActivity.class);
                intent.putExtra("msg", "这是一条来自 plugin1：TestActivity1 的消息！");
                startActivity(intent);
            }
        });

        btnJump2 = (Button) findViewById(R.id.btn_jump2);
        btnJump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity1.this, TestActivity1.class);
                startActivity(intent);
            }
        });
    }
}
