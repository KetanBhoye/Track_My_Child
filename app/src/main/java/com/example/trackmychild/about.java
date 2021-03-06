package com.example.trackmychild;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class about extends AppCompatActivity
{
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        /*ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("About");*/



        /*Toolbar toolbar = findViewById(R.id.action_bar);
        if (toolbar!= null)
        {
            toolbar.setTitle("About");
            toolbar.setTitleTextColor(Color.BLACK);
        }*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white,getTheme()));
        setSupportActionBar(toolbar);


        getWindow().setStatusBarColor(getResources().getColor(R.color.darkblue2, this.getTheme()));
        // getWindow().setNavigationBarColor(getResources().getColor(R.color.yellow,this.getTheme()));
    }
    // For back button on toolbar
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

}
