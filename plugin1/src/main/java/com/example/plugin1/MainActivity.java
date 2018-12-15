package com.example.plugin1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mypluginlibrary.BasePluginActivity;

public class MainActivity extends BasePluginActivity {
    private ImageView imageView;
    private Button btnJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
}
