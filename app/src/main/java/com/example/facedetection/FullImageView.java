package com.example.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class FullImageView extends AppCompatActivity {





    ImageView imgFull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        imgFull= findViewById(R.id.imgFull);

        Bitmap bitmap=null;
        try{
            bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        imgFull.setImageBitmap(bitmap);

    }

}
