package com.example.youcoolmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.youcoolmusic.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView img_developer;
    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        img_developer = (CircleImageView) findViewById(R.id.img_developer);
        img_developer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        click++;
        if(click == 2){
            img_developer.setImageResource(R.drawable.developer);
        }else if(click == 3){
            img_developer.setImageResource(R.drawable.developer1);
        }else if (click == 4){
            img_developer.setImageResource(R.drawable.developer2);
        }else if (click == 5){
            img_developer.setImageResource(R.drawable.myavatar);
            click = 0;
        }
    }
}
