package com.gjs.developresponsity.video.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.FrescoImageUtils;
import com.gjs.developresponsity.utils.VideoUtils;
import com.gjs.developresponsity.video.tools.VideoAndThumbLoader;
import com.gjs.developresponsity.video.view.CircleLoading;
import com.gjs.developresponsity.video.view.TextureVideoView;

import org.wlf.filedownloader.FileDownloader;

/**
 * <pre>
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018_7_6
 *     desc    : 视频播放
 *     version : 1.0
 * </pre>
 */

public class FullVideoPlayActivity extends AppCompatActivity {

    /**
     * 播放器
     **/
    private TextureVideoView full_video_view;
    /**
     * 首帧图
     **/
    private SimpleDraweeView full_video_thumbnail;
    /**
     * 下载失败布局
     **/
    private RelativeLayout video_down_fail_layout;
    /**
     * 整个页面布局
     **/
    private RelativeLayout full_video_act_layout;
    /**
     * 播放器布局
     **/
    private RelativeLayout video_play_layout;
    /**
     * 下载进度
     **/
    private CircleLoading surface_circle_load;
    /**
     * 退出提示
     **/
    private TextView touch_exit_hint;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_full_video_play);

        FileDownloader.registerDownloadStatusListener(VideoAndThumbLoader.getInstance());
        initView();
    }

    private void initView() {
        full_video_view = (TextureVideoView)findViewById(R.id.full_video_view);
        full_video_thumbnail = (SimpleDraweeView) findViewById(R.id.full_video_thumbnail);
        video_down_fail_layout = (RelativeLayout) findViewById(R.id.video_down_fail_layout);
        full_video_act_layout = (RelativeLayout) findViewById(R.id.full_video_act_layout);

        full_video_act_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                FullVideoPlayActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });

        video_play_layout = (RelativeLayout) findViewById(R.id.video_play_layout);
        RelativeLayout.LayoutParams layoutManager = (RelativeLayout.LayoutParams)video_play_layout.getLayoutParams();
        layoutManager.height = VideoUtils.getScreenWidth(FullVideoPlayActivity.this)*4/5;
        layoutManager.width = VideoUtils.getScreenWidth(FullVideoPlayActivity.this);
        full_video_view.setLayoutParams(layoutManager);

        surface_circle_load = (CircleLoading) findViewById(R.id.surface_circle_load);
        touch_exit_hint = (TextView) findViewById(R.id.touch_exit_hint);
        touch_exit_hint.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initPlay();
    }

    String mVideoPath;
    String mVideoUrl;

    private  void initData(){
        mVideoUrl = getIntent().getStringExtra("videourl");
        mVideoPath = getIntent().getStringExtra("videopath");
    }

    private void initPlay(){
        VideoAndThumbLoader.getInstance().fullVideoLoader(full_video_view,mVideoPath,mVideoUrl,full_video_thumbnail,surface_circle_load,video_down_fail_layout);
        full_video_view.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        full_video_thumbnail.setVisibility(View.GONE);
                    }
                }, 500);
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (touch_exit_hint.getVisibility() == View.GONE) {
                    touch_exit_hint.setVisibility(View.VISIBLE);
                }
                mp.start();
            }

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }
    /**
     * 首帧图的显示和隐藏
     *
     * @param thumbView
     * @param videoPath
     * @param textureVideoView
     */
    private void setThumbShow(SimpleDraweeView thumbView, String videoPath,
                              TextureVideoView textureVideoView) {
        if (textureVideoView != null) {
            if (!TextUtils.isEmpty(videoPath)) {
                if (VideoUtils.fileExists(videoPath) && textureVideoView.isPlaying()) {
                    thumbView.setVisibility(View.GONE);
                } else {
                    if (thumbView.getVisibility() == View.GONE) {
                        thumbView.setVisibility(View.VISIBLE);
                        if (textureVideoView != null && textureVideoView.isPlaying()) {
                            textureVideoView.stop();
                        }
                    }
                }
            } else {
                if (thumbView.getVisibility() == View.GONE) {
                    thumbView.setVisibility(View.VISIBLE);
                    if (textureVideoView != null && textureVideoView.isPlaying()) {
                        textureVideoView.stop();
                    }
                }
            }
        } else {
            thumbView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (full_video_view != null) {
            full_video_view.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (full_video_view != null) {
            full_video_view.stop();
        }
        //关闭界面时关闭云存储下载
//        VideoAndThumbLoader.getInstance().stopCloud();
    }

}
