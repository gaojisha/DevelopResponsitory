package com.gjs.developresponsity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test3();
    }

    private void test() {
        Log.i("test","1");
    }

    private void test2() {
        Log.i("test","2");
    }

    private void test3() {
        Log.i("test","3");
    }
}
