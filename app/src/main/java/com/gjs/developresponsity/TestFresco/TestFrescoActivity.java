package com.gjs.developresponsity.TestFresco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gjs.developresponsity.R;

public class TestFrescoActivity extends AppCompatActivity {

    private SimpleDraweeView test_fresco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fresco);

        test_fresco  = findViewById(R.id.test_fresco);

//        FrescoTestUtils.getFrescoTest().createLocalImageControllerBuilder(Environment.getExternalStorageDirectory() + "/mmexport1531374514188.jpg").build().showLocalImage(test_fresco);
        FrescoTestUtils.getFrescoTest().createWebImageControllerBuilder("https://images2015.cnblogs.com/blog/47685/201610/47685-20161017144827185-112405259.jpg").showWebImage(test_fresco);

    }
}
