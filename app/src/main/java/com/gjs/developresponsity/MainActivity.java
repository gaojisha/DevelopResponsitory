package com.gjs.developresponsity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gjs.developresponsity.activity.SwiperRevealLayoutActivity;
import com.gjs.developresponsity.activity.audio.AudioActivity;
import com.gjs.developresponsity.activity.image.ImageActivity;
import com.gjs.developresponsity.datastructure.LinearArray;
import com.gjs.developresponsity.utils.SortUtils;
import com.gjs.developresponsity.utils.permission.Permission;
import com.gjs.developresponsity.utils.permission.PermissionResult;

public class MainActivity extends Activity {

    private String[] REQUEST_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission.checkPermission(MainActivity.this, REQUEST_PERMISSIONS, new PermissionResult() {
            @Override
            public void success() {
                Toast.makeText(MainActivity.this,"获取权限成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail() {
                Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
            }
        });
    }


/**===========================================================分割线==============================================================================*/

    public void functionswiper(View v){
        startActivity(new Intent(MainActivity.this, SwiperRevealLayoutActivity.class));
    }

    public void imagetool(View v){
        startActivity(new Intent(MainActivity.this, ImageActivity.class));
    }

    public void audiotool(View v){
        startActivity(new Intent(MainActivity.this, AudioActivity.class));
    }

    private void sortUtiltest() {
        testLinearArrayInsert();
        testLinearArrayDelete();

        int[] sorData = {9,0,1,8,7,3,4,6,2,5};
        int[] bubblesortData = {9,0,1,8,7,3,4,6,2,5};
        int[] simpleSelectSortData = {9,0,1,8,7,3,4,6,2,5};
        int[] optimizeBubbleSortData = {9,0,1,8,7,3,4,6,2,5};
        int[] straightInsertionSortData = {9,0,1,8,7,3,4,6,2,5};
        int[] shellSortData = {9,0,1,8,7,3,4,6,2,5};
//        int[] shellSortData = {0,9,1,5,8,3,7,4,6,2};
        int[] heapSortData = {9,0,1,8,7,3,4,6,2,5};
        int[] mergingSortData = {9,0,1,8,7,3,4,6,2,5};
        int[] quickSortData = {9,0,1,8,7,3,4,6,2,5};

        int[] simpleData = SortUtils.getInstance().simpleSort(sorData);
        int[] bubbleData = SortUtils.getInstance().bubbleSort(bubblesortData);
        int[] simpleSelectData = SortUtils.getInstance().simpleSelectedSort(simpleSelectSortData);
        int[] optimizeBubbleData = SortUtils.getInstance().optimizeBubbleSort(optimizeBubbleSortData);
        int[] straightInsertionData = SortUtils.getInstance().straightInsertionSort(straightInsertionSortData);
        int[] shellData = SortUtils.getInstance().shellSort(shellSortData);
        int[] heapData = SortUtils.getInstance().heapSort(heapSortData);
        int[] mergingData = SortUtils.getInstance().mergingSort(mergingSortData);
        int[] quickData = SortUtils.getInstance().quickSort(quickSortData);

//        for (int i = 0 ; i < simpleData.length; i++) {
//            Log.i("developutil", "simpleSortlist : " + simpleData[i]);
//        }
//        for(int i = 0 ; i < bubbleData.length ; i++) {
//            Log.i("developutil", "bubbleSortlist : " + bubbleData[i]);
//        }
//        for(int i = 0 ; i < simpleSelectData.length ; i++) {
//            Log.i("developutil", "SimpleSelectSortlist : " + simpleSelectData[i]);
//        }
//        for(int i = 0 ; i < optimizeBubbleData.length ; i++) {
//            Log.i("developutil", "optimizeBubblelist : " + optimizeBubbleData[i]);
//        }
//        for(int i = 0 ; i < straightInsertionData.length ; i++) {
//            Log.i("developutil", "straightInsertionlist : " + straightInsertionData[i]);
//        }
//        for(int i = 0 ; i < shellData.length ; i++) {
//            Log.i("developutil", "shelllist : " + shellData[i]);
//        }
//        for(int i = 0 ; i < heapData.length ; i++) {
//            Log.i("developutil", "heaplist : " + heapData[i]);
//        }
        for(int i = 0 ; i < mergingData.length ; i++) {
            Log.i("developutil", "merginglist : " + mergingData[i]);
        }
//        for(int i = 0 ; i < quickData.length ; i++) {
//            Log.i("developutil", "merginglist : " + quickData[i]);
//        }

    }

    private void testLinearArrayDelete() {
        int[] data = {1,2,3,4,5,6,7,8};
        int listInsert = LinearArray.getInstance().listDelete(data, 4);
        if(listInsert == LinearArray.SUCCESS) {
            for (int i = 0; i < data.length; i++) {
                Log.i("MainActivity", " i : " + i + " data[i] : " + data[i]);
            }
        }
    }

    private void testLinearArrayInsert() {
        int[] data = {1,2,3,5,6,7,8};
        int listInsert = LinearArray.getInstance().listInsert(data, 4, 4);
        if(listInsert == LinearArray.SUCCESS) {
            for (int i = 0; i < data.length; i++) {
                Log.i("MainActivity", " i : " + i + " data[i] : " + data[i]);
            }
        }
    }
}
