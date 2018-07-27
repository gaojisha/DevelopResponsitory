package com.gjs.developresponsity.video.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.adapter.RecycleVideoAdapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;

import java.util.List;

public class VideoHistoryActivity extends AppCompatActivity {

    private final String videoUrl[] = {"http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4"
    ,"https://gslb.miaopai.com/stream/Zx429HzZ9-QCdorJMHX9JThMK0iByKG41y-v0g__.mp4?yx=&refer=weibo_app&Expires=1530760985&ssig=9sO%2BGTWyRH&KID=unistore"
    ,"https://gslb.miaopai.com/stream/RsSAxQGmQ-BjSDMCEJTk7iTIxdBt6f7m5UCEyQ__.mp4?yx=&refer=weibo_app&mpflag=32&mpr=1530717319&Expires=1530761558&ssig=5svPuVdQTx&KID=unistore"};
    private TextView tv_progress;//进度显示
    private ProgressBar progressBar;//进度条
    private Button downLoad;//下载按钮
    private Button pause;//暂停按钮
    private String path;//下载路径
    private int max;
    RecycleVideoAdapter mVideoDownAdapter;
    private List<DownloadFileInfo> mVideoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        prepareData();

        //获取RecycleView，videoList展示
        RecyclerView videoDownList = findViewById(R.id.recycler_video_list);
        videoDownList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //videoAdapter  视频列表适配器
        mVideoDownAdapter = new RecycleVideoAdapter();
        mVideoDownAdapter.setVideoList(this,mVideoList);
        //设置数据展示
        videoDownList.setAdapter(mVideoDownAdapter);

        FileDownloader.registerDownloadStatusListener(mVideoDownAdapter);
    }

    /**
     * onClick监听  添加录制视频
     * @param view
     */
    public void addnewvideo(View view){
        startActivity(new Intent(VideoHistoryActivity.this, VideoCreateActivity.class));
    }

    /**
     * 测试数据准备
     * @return 测试数据
     */
    private void prepareData() {
//        ArrayList<DownloadFileInfo>  videoList = new ArrayList();
        for(int i = 0; i < videoUrl.length; i++){
            FileDownloader.start(videoUrl[i]);
        }
        mVideoList = FileDownloader.getDownloadFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_down,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.action_pause_all){
//            if("全部暂停".equals(item.getTitle())) {
//                mVideoDownAdapter.pauseAll();
//                item.setTitle("全部开始");
//            } else if("全部开始".equals(item.getTitle())) {
//                mVideoDownAdapter.startAll();
//                item.setTitle("全部暂停");
//            }
//        }
//        return true;
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        FileDownloader.unregisterDownloadStatusListener(mVideoDownAdapter);
        super.onDestroy();
    }
}

