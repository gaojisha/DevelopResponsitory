package com.gjs.developresponsity.video.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.FrescoImageUtils;
import com.gjs.developresponsity.utils.VideoUtils;
import com.gjs.developresponsity.video.model.VideoHistoryBean;
import com.gjs.developresponsity.video.tools.NoDoubleClickListener;
import com.gjs.developresponsity.video.tools.ffmpeg.MP4Recorder;
import com.gjs.developresponsity.video.view.TextureVideoView;

import java.io.File;
import java.util.List;

/**
 * 聊天页面录制小视频
 */
public class IMRecordFragment extends BaseFragment {

    private VideoHistoryFragment mHistoryFragment;
    //用户id
    private long mUserId;
    /**
     * 保存的小视频路径
     */
    private String mPath;
    /**
     * 启动拍摄
     **/
    private ImageView mImageViewRecord;
    /**
     * 聚焦框
     **/
    private ImageView mImageViewFocus;
    /**
     * 聚焦框图片
     **/
    private Bitmap mBitmapFocus;

    private int mFocusWidth = 0;
    /**
     * 进度条
     **/
    private View mRecordIndicator;
    /**
     * 进度条布局
     **/
    private RelativeLayout mRelativeLayoutRecordProgressLayout;
    /**
     * 再拍一个的文本
     */
    private TextView mTextViewMore;
    /**
     * 上移取消
     */
//    private TextView mTextViewUpRelease;
    /**
     * 松开取消
     */
    private TextView mTextViewRelease;
    /**
     *
     */
    private TextView mTextViewFingerPrompt;
    /**
     * 双击聚焦
     */
    private TextView mTextViewDoubleClick;
    private boolean downStart;
    private SurfaceView mSurfacePreview;

    private FrameLayout mFrameRecordLayout;
    private TextView mTextViewOverflowMax;
    private int mWindowWidth = 0;

    private MP4Recorder mMP4Recorder;

    private VideoUtils.CAPTURE_STATE mState = VideoUtils.CAPTURE_STATE.EMPTY;
    //手指离开时间
    private long mPressStopTime;
    //手指按下时间
    private long mPressStartTime;

    private Rect mHotArea;
    // 7s超时
    private final int mExpireTime = 7000;

    private Animation mAnim;
    /**
     * 对焦动画
     */
    private Animation mFocusAnimation;
    private List<VideoHistoryBean> historyList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mImageViewFocus.setVisibility(View.GONE);
        }
    };

    //记录双击监听
    private int count = 0;  //点击次数
    //第一次点击的时间
    private long firclicktime = 0;
    //第二次点击的时间
    private long secclicktime = 0;
    private boolean tag = true;
    /**
     * 视频首帧图
     **/
    private SimpleDraweeView mImageViewPreview;

    private TextView mImRecordCancel;

    /**
     * 录制控制
     */
    private RelativeLayout im_video_operation;
    /**
     * 录制控制切换的是否发送界面
     */
    private RelativeLayout im_video_dialog;

    /**
     * 取消发送
     */
    private ImageView im_videodialog_no;
    /**
     * 确定发送
     */
    private ImageView im_videodialog_yes;
    /**
     * 重复预览已录制的视频
     */
    private TextureVideoView full_video_view;
    /**
     * 切换摄像头
     */
    private ImageView im_record_video_font;
    private TextView mImRecording;
    private boolean isShowPreView;
    //用于录制时关闭音乐，这里使用的是AudioManager的竞争AudioFocus的方式
    private AudioManager mAudioManager;

    @Override
    public int getLayoutId() {
        return R.layout.im_capture_video_fragment;
    }


    public boolean isOK = true;//true,表示当前可以继续，false表示不能继续

    @SuppressLint("NewApi")
    @Override
    public void createView(LayoutInflater inflater, ViewGroup container, View parentView, Bundle savedInstanceState) {
        mUserId = 1l;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mImageViewRecord = (ImageView) parentView.findViewById(R.id.imageview_start);
        VideoUtils.viewFadeOut(getActivity(), mImageViewRecord);
        mImageViewFocus = (ImageView) parentView.findViewById(R.id.imageview_focus);
        //计算聚焦图的宽度
        mBitmapFocus = BitmapFactory.decodeResource(getResources(), R.drawable.video_focus);
        mTextViewOverflowMax = (TextView) parentView.findViewById(R.id.textview_overflow_max);
        mFocusWidth = mBitmapFocus.getWidth();
        mRecordIndicator = parentView.findViewById(R.id.im_record_indicator);
        mRelativeLayoutRecordProgressLayout = (RelativeLayout) parentView.findViewById(R.id.im_record_progress_layout);
//        mTextViewUpRelease = (TextView) parentView.findViewById(R.id.textview_up_cancel);
        mTextViewRelease = (TextView) parentView.findViewById(R.id.textview_cancel);
        mTextViewFingerPrompt = (TextView) parentView.findViewById(R.id.textview_fingers);   //手指不要放开提示
        mTextViewMore = (TextView) parentView.findViewById(R.id.textview_more);
        mTextViewDoubleClick = (TextView) parentView.findViewById(R.id.textview_zoom);  //双击放大提示
        mTextViewDoubleClick.setVisibility(View.GONE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                VideoUtils.viewFadeOut(getActivity(), mTextViewDoubleClick);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageViewFocus.getLayoutParams();
                params.setMargins(mSurfacePreview.getWidth() / 2 - mFocusWidth / 2, mSurfacePreview.getHeight() / 2 - mFocusWidth / 2, 0, 0);
                VideoUtils.viewFadeOut(getActivity(), mImageViewFocus);
            }
        }, 2000);
        mSurfacePreview = (SurfaceView) parentView.findViewById(R.id.surfaceview_record);
        mFrameRecordLayout = (FrameLayout) parentView.findViewById(R.id.im_record_layout);
        //动态改变录制区域大小  宽高比 5:4
        mWindowWidth = VideoUtils.getScreenWidth(getActivity());
        ViewGroup.LayoutParams layoutParams = mFrameRecordLayout.getLayoutParams();
        layoutParams.height = mWindowWidth * 4 / 5;
        mFrameRecordLayout.setLayoutParams(layoutParams);
        mSurfacePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        changeFouce(event);
                        break;
                }
                return false;
            }
        });

        full_video_view = (TextureVideoView) parentView.findViewById(R.id.video_review);

        mImageViewPreview = (SimpleDraweeView) parentView.findViewById(R.id.imageview_preview);

        mImageViewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动查看历史界面
                showHistroyFragment();
            }
        });
        mImRecordCancel = (TextView) parentView.findViewById(R.id.im_record_cancel);
        mImRecordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelVideo();
                getFragmentManager().beginTransaction().remove(IMRecordFragment.this).commit();
            }
        });
        mImRecording = (TextView) parentView.findViewById(R.id.im_recording);

        im_record_video_font = (ImageView) parentView.findViewById(R.id.im_record_video_font);

        im_record_video_font.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                hideSendDialogView();
                stopVideo();
                //取消发送
                cancelVideo();
                mMP4Recorder.switchCamera();

//                mMP4Recorder.openMP4(mUserId, mExpireTime, new MP4Recorder.MP4RecorderDelegate() {
//                    public void onTimeout(String path) {
//                        stopAnimation();
//                        stopRecord(false);
//                    }
//
//                    public void onStarted() {
//                        recordProgressScale(mExpireTime);
//                    }
//
//                    public void onFinished(String path) {
//                        stopAnimation();
//                        showSendDialogView();
//                        mPath = path;
//                    }
//
//                    public void onFailed(int err) {
//                        stopAnimation();
//                        mState = VideoUtils.CAPTURE_STATE.EMPTY;
//                    }
//
//                    public void onCanceled() {
//                        stopAnimation();
//                        mState = VideoUtils.CAPTURE_STATE.EMPTY;
//                    }
//                });
            }
        });

        //初始化界面显示
        im_video_operation = (RelativeLayout) parentView.findViewById(R.id.im_video_operation_rl);
        im_video_dialog = (RelativeLayout) parentView.findViewById(R.id.im_video_dialog_rl);
        hideSendDialogView();
        stopVideo();
        im_videodialog_yes = (ImageView) parentView.findViewById(R.id.im_videodialog_yes);
        im_videodialog_no = (ImageView) parentView.findViewById(R.id.im_videodialog_no);
        im_videodialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSendDialogView();
                stopVideo();
                //确定发送
                sendVideo();
            }
        });
        im_videodialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSendDialogView();
                stopVideo();
                //取消发送
                cancelVideo();
            }
        });
    }

    private void showHistroyFragment() {
        mHistoryFragment = new VideoHistoryFragment();
        mHistoryFragment.setLifeCycleListener(new VideoHistoryFragment.LifeCycle() {
            @Override
            public void onCreate() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onHiddenChanged() {
                showHistoryList();
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onDestroy() {

            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(VideoHistoryFragment.FROM, VideoHistoryFragment.IM);
        mHistoryFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.frame_container, mHistoryFragment).commit();
        mHistoryFragment.setVideoHistoryListener(new VideoHistoryAdapter.VideoHistoryListener() {
            @Override
            public void sendVideo(String videoPath) {
                getFragmentManager().beginTransaction().remove(IMRecordFragment.this).commit();
                hideHistory();
                if (videoListener != null) {
                    videoListener.onVideoFinish(videoPath);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //竞争AudioFocus,停止其他音乐播放
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        showHistoryList();
    }

    private void showHistoryList() {
        isShowPreView = false;
        //获取历史列表数据
        historyList = VideoUtils.showVideoList(VideoUtils.video7DayFileName(VideoUtils.getRecordVideoPath()), false);
        if (historyList.size() > 0 && !TextUtils.isEmpty(historyList.get(historyList.size() - 1).getVideoName())) {

            String thumbnailName = historyList.get(historyList.size() - 1).getVideoName().
                    substring(0, historyList.get(historyList.size() - 1).getVideoName().lastIndexOf(".")) + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;

            File thumbnailFile = new File(VideoUtils.getRecordImagePath() + thumbnailName);
            if (thumbnailFile.exists()) {
                //加载首帧图
                isShowPreView = true;
                mImageViewPreview.setVisibility(View.VISIBLE);
                FrescoImageUtils.getInstance().createSdcardBuilder("file://" + VideoUtils.getRecordImagePath() + thumbnailName).setPlaceDrawable(R.drawable.small_video_circle_default).build().showSdcardImage(mImageViewPreview);
            }
        }
        if (!isShowPreView) {
            mImageViewPreview.setVisibility(View.GONE);
        }
    }


    public boolean isShowHistroy() {

        return mHistoryFragment != null && !mHistoryFragment.isHidden();
    }

    public void hideHistory() {
        showHistoryList();
        getFragmentManager().beginTransaction().hide(mHistoryFragment).commit();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mImRecording.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchPress(v, event);
            }
        });
        initMP4Recorder();

    }

    private void initMP4Recorder() {
        mMP4Recorder = new MP4Recorder() {
            protected SurfaceView findSurfaceView() {
                return mSurfacePreview;
            }

            protected float getWHRatio() {
                return 1.25f;
            }

            protected Activity getContext() {
                return getActivity();
            }
        };
        mMP4Recorder.onCreate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMP4Recorder.onPause();
        mAudioManager.abandonAudioFocus(null);
    }

    public boolean onTouchPress(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                if (mState != VideoUtils.CAPTURE_STATE.RECORDING) {
                    return true;
                }
                mState = VideoUtils.CAPTURE_STATE.TRY_STOPPING;
                mTextViewMore.setVisibility(View.GONE);
                mTextViewRelease.setVisibility(View.GONE);
//                mTextViewUpRelease.setVisibility(View.GONE);
                stopRecord(true);
                break;
            case MotionEvent.ACTION_UP:
                if (isOK) {
                    if (mState != VideoUtils.CAPTURE_STATE.RECORDING) {
                        return true;
                    }
                    mPressStopTime = System.currentTimeMillis();
                    mTextViewMore.setVisibility(View.GONE);
                    mTextViewRelease.setVisibility(View.GONE);
//                    mTextViewUpRelease.setVisibility(View.GONE);
                    cancelRecord(mPressStopTime - mPressStartTime);
                    mState = VideoUtils.CAPTURE_STATE.TRY_STOPPING;
                    stopRecord(!isFingerInArea(event));
                }
                break;
            case MotionEvent.ACTION_DOWN:
                isOK = true;
                downStart = true;
                if (mState != VideoUtils.CAPTURE_STATE.EMPTY) {
                    return true;
                }
                mPressStartTime = System.currentTimeMillis();
                mState = VideoUtils.CAPTURE_STATE.RECORDING;
//                showReleaseTips(isFingerInArea(event));
//                mTextViewUpRelease.setVisibility(View.VISIBLE);
                mTextViewFingerPrompt.setVisibility(View.GONE);
                startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
//                showReleaseTips(isFingerInArea(event));
                break;
        }
        return true;

    }

    //开始录制
    private void startRecord() {
        if (historyList != null && historyList.size() >= VideoUtils.MAX_VIDEO_NUM) {
//            mTextViewOverflowMax.setVisibility(View.VISIBLE);
            VideoUtils.viewFadeOut(getActivity(), mTextViewOverflowMax);
        }
        mImageViewPreview.setVisibility(View.GONE);
        mMP4Recorder.openMP4(mUserId, mExpireTime, new MP4Recorder.MP4RecorderDelegate() {
            public void onTimeout(String path) {
                isOK = false;
                stopAnimation();
                stopRecord(false);
            }

            public void onStarted() {
                recordProgressScale(mExpireTime);
            }

            public void onFinished(String path) {
                stopAnimation();
                showSendDialogView();
                mPath = path;
                playVideo(path);
            }

            public void onFailed(int err) {
                stopAnimation();
                mState = VideoUtils.CAPTURE_STATE.EMPTY;
            }

            public void onCanceled() {
                stopAnimation();
                mState = VideoUtils.CAPTURE_STATE.EMPTY;
            }
        });
    }

    /**
     * 开始预览
     *
     * @param path
     */
    private void playVideo(String path) {
        try {
//            mSurfacePreview.setVisibility(View.GONE);
            full_video_view.setVisibility(View.VISIBLE);
            full_video_view.setVideoPath(path);
            full_video_view.start();
            full_video_view.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                }

                @Override
                public void onCompletion(MediaPlayer mp) {
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

        } catch (Exception e) {

        }
    }

    /**
     * 停止预览
     */
    private void stopVideo() {
        try {
            full_video_view.setVisibility(View.GONE);
            full_video_view.stop();
//            mSurfacePreview.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }
    }

    /**
     * 视频录制动画   maxSec 录制时间=7s
     */
    private void recordProgressScale(int maxSec) {
        mRelativeLayoutRecordProgressLayout.setVisibility(View.VISIBLE);
        mAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.social_video_record_shrink);
        mAnim.setDuration(maxSec);
        mAnim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation anim) {
                mRelativeLayoutRecordProgressLayout.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation anim) {
            }

            public void onAnimationStart(Animation anim) {

            }
        });
        mRecordIndicator.startAnimation(mAnim);
    }

    private void stopAnimation() {
        if (mAnim == null) {
            return;
        }
        mAnim.cancel();
        mAnim = null;
    }

    /**
     * 用户手指是否在热点区域，更新对应的tips
     *
     * @param isFingerIn
     */
    private void showReleaseTips(boolean isFingerIn) {
        if (downStart) {
//            mTextViewUpRelease.setVisibility(isFingerIn ? View.VISIBLE : View.GONE);
            mTextViewRelease.setVisibility(isFingerIn ? View.GONE : View.VISIBLE);
            mRecordIndicator.setBackgroundColor(isFingerIn ? Color.parseColor("#ff6767") : Color.parseColor("#f7311c"));//进度条设置颜色变化
        }
    }

    /**
     * 按下时，手指是否在热点区域
     *
     * @param event
     * @return
     */
    private boolean isFingerInArea(MotionEvent event) {
        Rect rc = getHotArea();
        return rc.contains((int) event.getRawX(), (int) event.getRawY());
    }

    private Rect getHotArea() {
        if (mHotArea != null) {
            return mHotArea;
        }

        mHotArea = new Rect();
        int[] location = new int[2];
        mImRecording.getLocationOnScreen(location);

        mHotArea.left = location[0];
        mHotArea.top = location[1];
        mHotArea.right = mImRecording.getWidth() + mHotArea.left;
        mHotArea.bottom = mImRecording.getHeight() + mHotArea.top;
        return mHotArea;
    }


    private void stopRecord(boolean cancel) {
        mMP4Recorder.closeMP4(cancel);
        showHistoryList();
    }

    //实现 按住拍  少于1秒时放弃拍摄
    private void cancelRecord(long time) {
        if (time < 1000) {
            stopRecord(true);
            VideoUtils.viewFadeOut(getActivity(), mTextViewFingerPrompt);
            mMP4Recorder.deleteFile();
        }
    }

    long firstZoomTime, secondZoomTime;//用于快速点击时限制放大缩小焦距的控制器
    long firstFouceTime, secondFouceTime;//用于快速点击时限制对焦的控制器

    //双击放大缩小焦距
    public void changeFouce(MotionEvent event) {
        if (canFouce) {
            canFouce = false;
            secondFouceTime = System.currentTimeMillis();
            if (secondFouceTime - firstFouceTime > 700) {
                onTouchFouce(event);        //第一次点击时时聚焦框出现
            }
            firstFouceTime = System.currentTimeMillis();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canFouce = true;
                }
            }, 500);
        }
        count++;
        if (count == 1) {
            firclicktime = System.currentTimeMillis();
        } else if (count == 2) {
            secclicktime = System.currentTimeMillis();
            long l = secclicktime - firclicktime;
            if (l < 700) {
                if (canZoom) {
                    canZoom = false;
                    //双击事件
                    secondZoomTime = System.currentTimeMillis();
                    if (secondZoomTime - firstZoomTime > 700) {
                        if (tag) {
                            //表示第一次双击，camera放大焦距
                            mMP4Recorder.setZoom(1);
                            tag = false;
                        } else {
                            //表示第二次双击，camera 缩小焦距
                            mMP4Recorder.setZoom(2);
                            tag = true;
                        }
                    }
                    firstZoomTime = System.currentTimeMillis();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canZoom = true;
                        }
                    }, 700);
                }
                count = 0;
                firclicktime = 0;
                secclicktime = 0;
            } else {
                count = 1;
                firclicktime = secclicktime;
            }
        }
    }

    private boolean canZoom = true;
    private boolean canFouce = true;

    //定点聚焦
    private void onTouchFouce(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();
        Rect touchRect = new Rect((int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));
        //得到聚焦框layoutparams
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mImageViewFocus
                .getLayoutParams();
        int left = (int) (x - (mFocusWidth / 2));
        int top = (int) (y - (mFocusWidth / 2));
        if (left < 0) {
            left = 0;
        } else if ((x + mFocusWidth / 2) > mWindowWidth) {
            left = mWindowWidth - mFocusWidth;
        }
        if (top < 0) {
            top = 0;
        }
        if ((y + mFocusWidth / 2) > mWindowWidth * 4 / 5) {
            top = mWindowWidth * 4 / 5 - mFocusWidth;
        }
        lp.leftMargin = left;
        lp.topMargin = top;
        mImageViewFocus.setLayoutParams(lp);
        //触摸surfaceview聚焦框出现
        mImageViewFocus.setVisibility(View.VISIBLE);
//        mTextViewDoubleClick.setVisibility(View.VISIBLE);
        //实现点击surfaceview 定点聚焦  聚焦成功后聚焦框隐藏
        mMP4Recorder.focusOnRect(touchRect, new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                canFouce = true;
                if (success) {
                    mImageViewFocus.setVisibility(View.GONE);
//                    mTextViewDoubleClick.setVisibility(View.GONE);
                }
            }
        });
        if (mFocusAnimation == null) {
            //设置缩放动画
            mFocusAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.record_focus);
        }
        //开始动画
        mImageViewFocus.startAnimation(mFocusAnimation);
        mHandler.sendEmptyMessageDelayed(0, 2000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMP4Recorder.onDestroy();
    }

    public interface VideoListener {
        void onVideoFinish(String path);

    }

    private VideoListener videoListener;

    public VideoListener getVideoListener() {
        return videoListener;
    }

    public void setVideoListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }

    @SuppressLint("NewApi")
    private void imFinishedVideo(String videoPath) {
        if (mState != VideoUtils.CAPTURE_STATE.RECORDING) {
            getFragmentManager().beginTransaction().remove(this).commit();
        } else {
            //松手再拍一个
            VideoUtils.viewFadeOut(getContext(), mTextViewMore);
            mTextViewRelease.setVisibility(View.GONE);
            mRelativeLayoutRecordProgressLayout.setVisibility(View.GONE);
//            mTextViewUpRelease.setVisibility(View.GONE);
            downStart = false;
            mImageViewPreview.setVisibility(View.VISIBLE);
        }
        String imagePath = VideoUtils.getFirstImagePath(videoPath);
        FrescoImageUtils.getInstance().createSdcardBuilder("file://" + imagePath).setPlaceDrawable(R.drawable.small_video_circle_default).build().showSdcardImage(mImageViewPreview);
        //上传视频
        if (videoListener != null) {
            videoListener.onVideoFinish(videoPath);
        }

    }


    /**
     * 显示发送小视频dialog
     */
    public void showSendDialogView() {
        im_video_operation.setVisibility(View.GONE);
        im_video_dialog.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏发送小视频dialog
     */
    public void hideSendDialogView() {
        im_video_operation.setVisibility(View.VISIBLE);
        im_video_dialog.setVisibility(View.GONE);
    }

    /**
     * 点击确定发送小视频
     */
    public void sendVideo() {
        mState = VideoUtils.CAPTURE_STATE.EMPTY;
        imFinishedVideo(mPath);
    }

    /**
     * 点击取消发送小视频
     */
    public void cancelVideo() {
        stopAnimation();
        mState = VideoUtils.CAPTURE_STATE.EMPTY;


        mTextViewMore.setVisibility(View.GONE);
        mTextViewRelease.setVisibility(View.GONE);
//        mTextViewUpRelease.setVisibility(View.GONE);
        mMP4Recorder.deleteFile();
        stopRecord(true);
    }

    /**
     * BaseFragment实现的广播相关的方法，这里没用到  start
     **/
    @Override
    protected void setBroadcaseFilter(IntentFilter filter) {

    }

    @Override
    protected boolean registerReceiver() {
        return false;
    }

    @Override
    public void onReceive(String action, int type, Bundle bundle) {

    }

    /**BaseFragment实现的广播相关的方法，这里没用到  end**/
}
