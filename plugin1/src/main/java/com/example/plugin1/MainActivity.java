package com.example.plugin1;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.mypluginlibrary.BasePluginActivity;

public class MainActivity extends BasePluginActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.plugin_img);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
    }
}
