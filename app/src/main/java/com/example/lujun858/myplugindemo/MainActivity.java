package com.example.lujun858.myplugindemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private Button jumpBtn;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jumpBtn = (Button) findViewById(R.id.btn_jump2_plugin_activity);
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
}
