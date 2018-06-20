package com.gjs.developresponsity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.RecycleVideoAdapter;
import com.gjs.developresponsity.model.VideoInfo;

import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DbTestActivity extends AppCompatActivity {

    private final String videoUrl = "http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4";
    private TextView tv_progress;//进度显示
    private ProgressBar progressBar;//进度条
    private Button downLoad;//下载按钮
    private Button pause;//暂停按钮
    private String path;//下载路径
    private int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        prepareData();

        //获取RecycleView，videoList展示
        RecyclerView videoDownList = findViewById(R.id.recycler_video_list);
        videoDownList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //videoAdapter  视频列表适配器
        RecycleVideoAdapter videoDownAdapter = new RecycleVideoAdapter();
        videoDownAdapter.setVideoList(this,prepareData());

        //设置数据展示
        videoDownList.setAdapter(videoDownAdapter);
    }

    /**
     * 测试数据准备
     * @return 测试数据
     */
    private ArrayList<VideoInfo> prepareData() {
        ArrayList<VideoInfo>  videoList = new ArrayList();
        for(int i = 0; i < 20; i++){
            videoList.add(new VideoInfo(videoUrl,"Video : " + i, videoUrl,"videoDescription : " + i));
        }
        return videoList;
    }

}

