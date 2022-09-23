package com.example.facedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;

import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button btnDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRoutinePermissions();
        btnDetect= findViewById(R.id.btnDetect);
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,LivePreviewActivity.class);
                startActivity(i);
            }
        });




    }

    //request

    public void requestRoutinePermissions(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

    }
}