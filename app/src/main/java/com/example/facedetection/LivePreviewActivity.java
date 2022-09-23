package com.example.facedetection;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
        implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private static final String OBJECT_DETECTION = "Object Detection";
    private static final String OBJECT_DETECTION_CUSTOM = "Custom Object Detection";
    private static final String CUSTOM_AUTOML_OBJECT_DETECTION =
            "Custom AutoML Object Detection (Flower)";
    private static final String FACE_DETECTION = "Face Detection";
    private static final String BARCODE_SCANNING = "Barcode Scanning";
    private static final String IMAGE_LABELING = "Image Labeling";
    private static final String IMAGE_LABELING_CUSTOM = "Custom Image Labeling (Birds)";
    private static final String CUSTOM_AUTOML_LABELING = "Custom AutoML Image Labeling (Flower)";
    private static final String POSE_DETECTION = "Pose Detection";
    private static final String SELFIE_SEGMENTATION = "Selfie Segmentation";
    private static final String TEXT_RECOGNITION_LATIN = "Text Recognition Latin";
    private static final String TEXT_RECOGNITION_CHINESE = "Text Recognition Chinese (Beta)";
    private static final String TEXT_RECOGNITION_DEVANAGARI = "Text Recognition Devanagari (Beta)";
    private static final String TEXT_RECOGNITION_JAPANESE = "Text Recognition Japanese (Beta)";
    private static final String TEXT_RECOGNITION_KOREAN = "Text Recognition Korean (Beta)";

    private static final String TAG = "LivePreviewActivity";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private String selectedModel = OBJECT_DETECTION;
    ImageView imgFlipCamera;
    ImageView imgCapture;
    ImageView capturedImage;
    ImageView imgDone;
    private boolean isFrontFacing=true;
    Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_vision_live_preview);
        imgFlipCamera=findViewById(R.id.imgFlipCamera);
        imgCapture=findViewById(R.id.imgCapture);
        imgDone=findViewById(R.id.imgDone);
        capturedImage=findViewById(R.id.capturedImage);

        imgFlipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFrontFacing = !isFrontFacing;
                toggleCamera();
            }
        });

        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = loadBitmapFrontView(graphicOverlay);
                capturedImage.setImageBitmap(bitmap);
                imgDone.setVisibility(View.VISIBLE);
                createImageFrontBitmap(bitmap);

            }
        });

        capturedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap != null){
                    Intent i=new Intent(LivePreviewActivity.this,FullImageView.class);
//          Toast.makeText(getApplicationContext(),"Face detected",Toast.LENGTH_SHORT).show();

                    startActivity(i);
                }
            }
        });

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }



        createCameraSource(selectedModel);
    }

    public String createImageFrontBitmap(Bitmap bitmap){
        String fileName="myImage";
        try{
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            FileOutputStream fo= openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        }
        catch(Exception e){
            e.printStackTrace();
            fileName=null;
        }
        return fileName;
    }


    public static Bitmap loadBitmapFrontView(View v){
        Bitmap b= Bitmap.createBitmap(v.getWidth(),v.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas c= new Canvas(b);
        v.draw(c);

        return b;
    }

    private void toggleCamera(){
        Log log;
        Log.d(TAG, "Set facing");
        if(cameraSource != null){
            if(isFrontFacing){
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            }
            else{
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }

    @Override
    public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedModel = parent.getItemAtPosition(pos).toString();
        Log.d(TAG, "Selected model: " + selectedModel);
        preview.stop();
        createCameraSource(selectedModel);
        startCameraSource();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        //Initializing camera

        Log.i(TAG, "Using Face Detector Processor");
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this, new onFaceDetectListener() {
            @Override
            public void onFaceDetector(Boolean isDetected) {
                if(isDetected){
                    imgCapture.setImageResource(R.drawable.ic_baseline_camera_white);
//          Toast.makeText(getApplication(),
//                  "Face Detected", Toast.LENGTH_SHORT).show();

                }
                else if(!isDetected){
                    imgCapture.setImageResource(R.drawable.ic_baseline_camera_gray);
//          Toast.makeText(getApplication(),
//                  "No Face Detected", Toast.LENGTH_SHORT).show();

                }

//        if(isDetected){
//          Toast.makeText(getApplication(),
//                  "Face Detected", Toast.LENGTH_SHORT).show();
//
//        }
//        else{
//          Toast.makeText(getApplication(),
//                  "No Face Detected", Toast.LENGTH_SHORT).show();
//        }

            }
        }));


    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource(selectedModel);
        startCameraSource();
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}