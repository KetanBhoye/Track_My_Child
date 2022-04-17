package com.example.trackmychild;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Spalsh_Screen extends AppCompatActivity
{
    private static int SPLASH_SCREEN = 2000;

    Animation topAnim,bottomAnim;
    ImageView imageView;
    TextView logo,slogan;
    DatabaseHelper databaseHelper;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);

        // topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        //  bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        databaseHelper = new DatabaseHelper(this);

        Cursor res = databaseHelper.getData("1");
        if (res.moveToFirst())
            name = res.getString(1);


        // imageView.setAnimation(topAnim);
        //  logo.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (name == null) {
                    Intent intent = new Intent(Spalsh_Screen.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Spalsh_Screen.this, Child_tracklocation.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN);
    }
}