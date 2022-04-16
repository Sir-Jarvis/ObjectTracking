package com.example.imagepro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

public class StoragePredictionActivity extends AppCompatActivity {
    private Button select_image;
    private ImageView image_v;
    private objectDetectorClass objectDetectorClass;
    int SELECT_PICTURE=200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_prediction);

        // Define select_image button and image_view

        select_image=findViewById(R.id.select_image);
        image_v=findViewById(R.id.image_v);
        try{
            // input size is 320 for this model
            // model and label parapmeters
            objectDetectorClass=new objectDetectorClass(getAssets(),"custom_model.tflite","custom_label.txt",320);
            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_chooser();
            }
        });
    }

    private void image_chooser() {
        // This function will choose image from phone storage
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // Success 200
        startActivityForResult(Intent.createChooser(i, "Selectionner une image"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if(requestCode==SELECT_PICTURE) {
                Uri selectedImageUri=data.getData();
                if (selectedImageUri != null) {
                    Bitmap bitmap=null;
                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Convert bitmap image to Mat image
                    //CV_8UC4 : RGBA image
                    // CV_8UC1 = Grayscale image

                    Mat selected_image=new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(bitmap, selected_image);

                    // Retourne l'image annotée grace à la détection
                    selected_image=objectDetectorClass.recognizePhoto(selected_image);

                    // Convert mat image to bitmap
                    Bitmap bitmap1=null;
                    bitmap1=Bitmap.createBitmap(selected_image.cols(), selected_image.rows(), Bitmap.Config.ARGB_8888);

                    //          input           output
                    Utils.matToBitmap(selected_image,bitmap1);
                    // now set bitmap to imageView
                    image_v.setImageBitmap(bitmap1);
                }
            }
        }
    }
}



