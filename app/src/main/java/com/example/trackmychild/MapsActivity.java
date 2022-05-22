package com.example.trackmychild;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MapsActivity extends AppCompatActivity
{
    Button driver,student;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        driver = findViewById(R.id.driver);
        student = findViewById(R.id.student);


        driver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MapsActivity.this,driver_login.class);
                startActivity(intent);
                finish();
            }
        });

        student.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)

            {
                Intent intent = new Intent(MapsActivity.this, Child_login.class);
                startActivity(intent);



            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Child Tracking System");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white,getTheme()));
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(getResources().getColor(R.color.darkblue2, this.getTheme()));
        // getWindow().setNavigationBarColor(getResources().getColor(R.color.yellow,this.getTheme()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_withoutroute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.share)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        else
        {
            Intent intent = new Intent(MapsActivity.this,about.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}