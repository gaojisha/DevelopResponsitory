package com.gjs.developresponsity.activity.image;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.image.ImageTools;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String imagePath = "/storage/emulated/0/icbcim/3DE43B3CC2257CFDFF9D58EB3612BF2E/21CB454C46482C4A10940AE4F9C74083/image/9B/3B/thumb_9B3BDFAB6DA7C77720A6E61FFAE44AC4";
        ImageTools.getCommpressImage(imagePath, ImageTools.Quality.BIG);
    }
}
