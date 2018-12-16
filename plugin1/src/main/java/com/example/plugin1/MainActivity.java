package com.example.plugin1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypluginlibrary.BasePluginActivity;

public class MainActivity extends BasePluginActivity {
    private ImageView imageView;
    private Button btnJump;
    private TextView subTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("JLu", "plugin: MainActivity  onCreate");

        setContentView(R.layout.activity_main);

//        imageView = (ImageView) findViewById(R.id.plugin_img);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));

        btnJump = (Button) findViewById(R.id.btn_jump);
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestActivity1.class);
                startActivity(intent);
            }
        });

        subTitle = (TextView) findViewById(R.id.sub_title);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("JLu", "plugin: MainActivity  onNewIntent + intent= " + intent);
        String msg = intent.getStringExtra("msg");
        subTitle.setText("onNewIntent 接收消息：" + msg);
    }
}
