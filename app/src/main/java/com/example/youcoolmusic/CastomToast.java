package com.example.youcoolmusic;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.View.inflate;

public class CastomToast {

    public void showToas(Context context, String text, boolean isOkay){
        View view = inflate(context, R.layout.castom_toast, null);
        ImageView imageView = view.findViewById(R.id.imageToast);
        if(isOkay){
            imageView.setImageResource(R.drawable.animeokay);
        }else{
            imageView.setImageResource(R.drawable.nointernet);
        }
        TextView textView = view.findViewById(R.id.textToast);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
