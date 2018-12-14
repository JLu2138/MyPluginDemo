package com.example.mypluginlibrary;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class BasePluginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(layoutResID, null);
        setContentView(view);

    }

    @Override
    public Resources getResources() {
        return getApplicationContext().getResources();
    }
}
