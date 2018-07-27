package com.gjs.developresponsity.video.tools.ffmpeg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.gjs.developresponsity.video.tools.log.Trace;

import java.io.File;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/5/24.
 * 对外调用使用的界面接口
 */
@SuppressWarnings("deprecation")
public abstract class MP4Recorder implements Camera.PreviewCallback, Callback {


    /**
     * 摄像头个数
     */
    private int numberOfCameras;

    /**
     * 此参数用来监控当前前后摄像头是否旋转，大于0表示需要翻转，小于或等于0表示不需要，默认不需要
     */
    public int rorateID = 0;

    public static final int DESIRE_WIDTH = 480;    // 截取后视频的目标宽度
    private int mCameraId = 0;
    private Camera mCamera;
    private CamcorderProfile mProfile;
    private Camera.Parameters mParameters;
    private int mDesiredPreviewWidth;           // camera 输出大小
    private int mDesiredPreviewHeight;
    private int mClipWidth;                      // camera 截取之后的大小
    private int mClipHeight;
    private String mOutputFileName;
    private long mMaxVideoDurationInMs; // 最长录制时间
    private SurfaceTexture mDummyTexture;
    private SurfaceHolder mSurfaceHolder;
    private CameraRefresher mImgRefresh = new CameraRefresher();
    private int mSurfaceWidth, mSurfaceHeight;

    private MP4Writer mWriter = new MP4Writer();
    private MP4AudioRecorder mAudioRecorder = new MP4AudioRecorder();
    private RecordParams mParams;

    private volatile long mAudioTimestamp = 0L;
    private volatile long mAudioTimeRecorded;
    private long mLastAudioTimestamp = 0L;
    private long mVideoTimestamp = 0L;
    private long firstTime = 0;
    private long totalTime = 0;
    private long frameTime = 0L;

    private boolean isRecordingStarted = false;
    private Handler mHandler = new Handler();
    private MP4RecorderDelegate mDelegate;
    private boolean mMP4Opened = false;


    /**
     * Activity 创建时调用
     */
    public void onCreate() {
        Trace.T();
        initCtrls();
        initCamera();
    }

    public void onPause() {
        Trace.T();
        stopPreview();
    }

    public void onDestroy() {
        Trace.T();
        closeCamera();
    }

    /**
     * 返回SurfaceView显示控件
     *
     * @return
     */
    protected abstract SurfaceView findSurfaceView();

    /**
     * 返回截取后视频的宽高比
     *
     * @return w/h
     */
    protected abstract float getWHRatio();

    /**
     * 返回绑定的
     *
     * @return
     */
    protected abstract Activity getContext();

    private void initCtrls() {
        findSurfaceView().getHolder().addCallback(this);
    }

    @SuppressLint("NewApi")
    private void initCamera() {
        numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = i;
            }
        }
        Trace.T();
        int quality = getLowVideoQuality();
        mProfile = CamcorderProfile.get(mCameraId, quality);
        openCamera();
        getDesiredPreviewSize();
    }

    @SuppressLint("NewApi")
    private void openCamera() {
        Trace.T();
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            Trace.T("error");
        }
    }

    /**
     * 进行前后摄像头的切换，切换后开启运行
     */
    @SuppressLint("NewApi")
    public void switchCamera() {

        if (mCamera == null) {
            return;
        }
        try {
            Trace.T();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            Trace.T("error");
            e.printStackTrace();
        }


        mCameraId = (mCameraId + 1) % numberOfCameras;

        int quality = getLowVideoQuality();
        mProfile = CamcorderProfile.get(mCameraId, quality);
        openCamera();
        getDesiredPreviewSize();
        startPreview();
    }

    //使用聚焦区域实现定点聚焦功能
    public void focusOnRect(Rect rect, Camera.AutoFocusCallback callback) {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters(); // 先获取当前相机的参数配置对象
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
                if (parameters.getMaxNumFocusAreas() > 0) {
                    List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                    focusAreas.add(new Camera.Area(rect, 1000));
                    parameters.setFocusAreas(focusAreas);
                }
                mCamera.cancelAutoFocus(); // 先要取消掉进程中所有的功能
                mCamera.setParameters(parameters); // 一定要记得把相应参数设置给相机
                mCamera.autoFocus(callback);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void setZoom(int zoomKey) {
        //判断是否支持变焦
        try {
            if (mParameters.isZoomSupported()) {
                int zoomValue = mParameters.getZoom();
                int maxZoom = mParameters.getMaxZoom();
                switch (zoomKey) {
                    case 2:
                        zoomValue = 0;
                        break;
                    case 1:
                        zoomValue = maxZoom / 2;
                        break;
                    default:
                        break;
                }
//                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mParameters.setZoom(zoomValue);
                mCamera.setParameters(mParameters);
            }
        } catch (Exception e) {
        }

    }

    private void setDefaultPreviewSize() {
        Trace.T();
        setDesireSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
    }

    @SuppressLint("NewApi")
    private void getDesiredPreviewSize() {
        mParameters = mCamera.getParameters();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            setDefaultPreviewSize();
            return;
        }
        List<Size> sizes = mParameters.getSupportedPreviewSizes();
        Size previewSize = mParameters.getPreviewSize();
        if (sizes == null) {
            setDefaultPreviewSize();
            return;
        }
        Size size = getPreviewSize(sizes, DESIRE_WIDTH, previewSize);
        for (Size s : sizes) {
            Trace.T("camera support: " + s.width + " " + s.height);
        }
        if (size == null) {
            setDefaultPreviewSize();
            return;
        }
        setDesireSize(size.width, size.height);
        Log.d("---------", "-------------使用的分辨率为+" + size.width + "    " + size.height);
    }

    public Size getPreviewSize(List<Size> list, int th, Size previewSize) {
        Collections.sort(list, new Comparator<Size>() {
            public int compare(Size lhs, Size rhs) {
                return lhs.width * lhs.height - rhs.width * rhs.height;
            }
        });
        Size size = null;
        for (int i = 0; i < list.size(); i++) {
            size = list.get(i);
            if (size.width * size.height != previewSize.width * previewSize.height) {
                continue;
            }
            if ((size.width > th) && equalRate(size, 1.33f)) {
                break;
            }
        }
        return size;
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }

    private void setDesireSize(int w, int h) {
        if ("MX4 Pro".equals(Build.MODEL)) {
            //对MX4 Pro进行了专门适配
            w = 1920;
            h = 1080;
        }
        mDesiredPreviewWidth = w;
        mDesiredPreviewHeight = h;
        Trace.T("capture " + mDesiredPreviewWidth + " " + mDesiredPreviewHeight);

        mClipWidth = mDesiredPreviewHeight;
        mClipHeight = (int) (mClipWidth / getWHRatio());
        mClipHeight = mClipHeight / 4 * 4;
        Trace.T("desire " + mClipWidth + " " + mClipHeight);
    }

    private int getLowVideoQuality() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return CamcorderProfile.QUALITY_480P;
        } else {
            return CamcorderProfile.QUALITY_LOW;
        }
    }

    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    @SuppressLint("NewApi")
    private void setCameraParameters() {
        Trace.T("preview size: " + mDesiredPreviewWidth + " " + mDesiredPreviewHeight);
        Trace.T("preview rate: " + mProfile.videoFrameRate);
        mParameters.setPreviewSize(mDesiredPreviewWidth, mDesiredPreviewHeight);
        mParameters.setPreviewFrameRate(mProfile.videoFrameRate);

        // Set continuous autofocus.
        List<String> supportedFocus = mParameters.getSupportedFocusModes();
        if (isSupported(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, supportedFocus)) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        if (!"GT-I9508".equals(Build.MODEL)) {
            mParameters.set("recording-hint", "true");
        }

        // level <= 14
        String vstabSupported = mParameters.get("video-stabilization-supported");
        if ("true".equals(vstabSupported)) {
            mParameters.set("video-stabilization", "true");
        }

        if (Build.VERSION.SDK_INT >= 15 && mParameters.isVideoStabilizationSupported()) {
            mParameters.setVideoStabilization(true);
        }

        mParameters.setPreviewFormat(ImageFormat.NV21);

        try {
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
            Trace.T("error");
        }
    }

    @SuppressLint("NewApi")
    private void startPreview() {
        Trace.T();
        try {
            mCamera.stopPreview();
            setCameraParameters();

            // 开启底层图像变换的环境
            mWriter.openPreview(mDesiredPreviewWidth, mDesiredPreviewHeight, mClipWidth, mClipHeight, mCameraId);
            mCamera.setPreviewCallback(this);

            mDummyTexture = new SurfaceTexture(42);
            mCamera.setPreviewTexture(mDummyTexture);
            mCamera.startPreview();
            startCameraRefresher();
        } catch (Exception e) {
            e.printStackTrace();
            Trace.T("error");
        }
    }

    private void startCameraRefresher() {
        Trace.T();
        mImgRefresh.start(mClipWidth, mClipHeight);
    }

    /**
     * 开启视频录制
     *
     * @param userId   用户Id
     * @param expire   最大录制时常 （毫秒）
     * @param delegate 回调通知
     */
    public void openMP4(long userId, int expire, MP4RecorderDelegate delegate) {
        mDelegate = delegate;
        mMaxVideoDurationInMs = expire;

        pauseAudioPlayback();
        keepScreenOn();
        resetTimestamp();

        mOutputFileName = getOutputFile(userId);
        RecordParams p = mParams = initMP4Recorder();
        // 开启底层视频录制
        mMP4Opened = true;
        Log.i("========", "mCameraId=======" + mCameraId);
        mWriter.openMP4File(mOutputFileName,
                mDesiredPreviewWidth, mDesiredPreviewHeight, mClipWidth, mClipHeight,
                p.VideoFrameRate, p.VideoBitrate, p.VideoQuality,
                p.AudioBitrate, p.AudioSampleRate, 1, p.AudioQuality, mCameraId);

        // 开启语音录制
        mAudioRecorder.start();
    }

    private void resetTimestamp() {
        mAudioTimestamp = 0L;
        mAudioTimeRecorded = 0;
        mLastAudioTimestamp = 0L;
        mVideoTimestamp = 0L;
        firstTime = 0;
        totalTime = 0;
        frameTime = 0L;
    }

    private RecordParams initMP4Recorder() {
        RecordParams p = new RecordParams();
        p.AudioBitrate = 128000;            // 128000 高 96000 低
        p.AudioQuality = 5;                    // 0 高  5中  20低
        p.AudioSampleRate = 44100;            // 采样率

        p.VideoFrameRate = 30;                // 帧率
        p.VideoQuality = 13;                    // 0 高  5中  20低
        p.VideoBitrate = 1000000;            // 1000000
        return p;
    }

    // 根据当前设定的参数，初始化本地参数
    private void initMP4Params(RecordParams p) {
        frameTime = (1000000L / p.VideoFrameRate);
        firstTime = System.currentTimeMillis();
        isRecordingStarted = true;
        onStarted();
    }

    private void stopPreview() {
        Trace.T();
        if (mCamera == null) {
            return;
        }
        closeMP4(true);
    }

    public void closeMP4(boolean cancel) {
        if (mCamera == null) {
            Trace.T("not inited");
            return;
        }

        if (mAudioRecorder != null) {
            mAudioRecorder.stop();
        }
        boolean started = isRecordingStarted;
        isRecordingStarted = false;

//    	if (!cancel && started) {
//	        mCamera.stopPreview();
//	        mImgRefresh.stop();
//    	}

        if (!started) {
            Trace.T("not start");
            if (mMP4Opened) {
                mWriter.closeMP4(false);
                mMP4Opened = false;
            }
            onCanceled();
            return;
        }

        Trace.T();
        mWriter.closeMP4(cancel ? false : true);
        mMP4Opened = false;

        if (cancel) {
            deleteFile();
            onCanceled();
        } else {
            onFinished();
        }
    }

    public void deleteFile() {
        if (TextUtils.isEmpty(mOutputFileName)) {
            return;
        }

        Trace.T();
        File f = new File(mOutputFileName);
        if (f.exists()) {
            f.delete();
        }
        mOutputFileName = null;
    }

    private void closeCamera() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCamera == null) {
                    return;
                }
                try {
                    Trace.T();
                    mCamera.stopPreview();
                    mCamera.setPreviewCallback(null);
                    mCamera.release();
                    mCamera = null;
                } catch (Exception e) {
                    Trace.T("error");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private String getOutputFile(long userId) {

        return getOutputPath(userId) + System.currentTimeMillis() + ".mp4";


    }

    private String getOutputPath(long userId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            String SD_PATH = Environment
                    .getExternalStorageDirectory().getAbsolutePath();
            String BASE_UPLOAD_VIDEO_PATH = SD_PATH + "/icbcim/video/" + userId + "/";
            File file = new File(BASE_UPLOAD_VIDEO_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            Log.i("path", BASE_UPLOAD_VIDEO_PATH);
            return BASE_UPLOAD_VIDEO_PATH;
        }
        return null;
    }

    /**
     * 摄像头回调数据
     *
     * @param img
     * @param camera
     */
    public void onPreviewFrame(byte[] img, Camera camera) {
        mWriter.addVideo(img, 0, 0, 270, calcVideoTime());
        mImgRefresh.onFrame();    // 从底层获取截取旋转后的数据
    }

    private long calcVideoTime() {
        long frameTimeStamp = 0L;
        if (!isRecordingStarted || mAudioTimestamp >= 1000 * mMaxVideoDurationInMs) {
            return frameTimeStamp;
        }

        if (mAudioTimestamp == 0L && firstTime > 0L) {
            frameTimeStamp = 1000L * (System.currentTimeMillis() - firstTime);
        } else if (mLastAudioTimestamp == mAudioTimestamp) {
            frameTimeStamp = mAudioTimestamp + frameTime;
        } else {
            long l2 = (System.nanoTime() - mAudioTimeRecorded) / 1000L;
            frameTimeStamp = l2 + mAudioTimestamp;
            mLastAudioTimestamp = mAudioTimestamp;
        }

        mVideoTimestamp += frameTime;
        Trace.T(mVideoTimestamp + " " + frameTimeStamp);
        if (frameTimeStamp > mVideoTimestamp) {
            mVideoTimestamp = frameTimeStamp;
        }
        return frameTimeStamp;
    }

    /**
     * Make sure we're not recording music playing in the background,<br>
     * ask the MediaPlaybackService to pause playback.
     */
    private void pauseAudioPlayback() {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");

        getContext().sendBroadcast(i);
    }

    private void keepScreenOn() {
        getContext().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getContext().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private class CameraRefresher {
        List<ShortBuffer> mEmpty = new ArrayList<ShortBuffer>();
        LinkedBlockingQueue<ShortBuffer> mFull = new LinkedBlockingQueue<ShortBuffer>();

        boolean mRunning;
        Thread mThread;
        int mAlloced = 0;               // 当前内存分配块数
        int mMaxAlloc = 4;              // 内存最大分配块数
        int mWidth;                     // 数据宽度
        int mHeight;                    // 数据高度
        Bitmap mBmp;                    // 用户绘制的ＢＭＰ
        Rect mBmpRC, mSurfaceRC;        // 位图位置，surface绘制位置

        /**
         * 开启绘制图片
         *
         * @param w // 绘制图片的宽高
         * @param h
         */
        public void start(int w, int h) {
            if (mRunning || mThread != null) {
                Trace.T("cancel");
                return;
            }

            mWidth = w;
            mHeight = h;
            mRunning = true;
            Trace.T(w + " " + h);

            if (mBmp == null) {
                mBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
                mBmpRC = new Rect(0, 0, mWidth, mHeight);
            }

            if (mSurfaceRC == null) {
                mSurfaceRC = new Rect(0, 0, mSurfaceWidth, mSurfaceHeight);
            }

            mThread = new Thread() {
                public void run() {
                    onRun();
                }
            };
            mThread.start();
        }

        public void stop() {
            Trace.T();
            if (!mRunning) {
                return;
            }

            mRunning = false;

            try {
                mThread.join(100);
            } catch (Exception e) {
            }

            mThread = null;
            mFull.clear();
            mEmpty.clear();

            if (mBmp != null) {
                mBmp.recycle();
                mBmp = null;
            }
        }

        public void onSurfaceChanged() {
            mSurfaceRC = new Rect(0, 0, mSurfaceWidth, mSurfaceHeight);
        }

        public void onFrame() {
            ShortBuffer rgb = getEmpty();
            if (rgb == null) {
                Trace.T("drop");
                return;
            }

            rgb.rewind();
            if (!mWriter.getRGB565(rgb.array())) {
                addEmpty(rgb);
                Trace.T("not prepare");
                return;
            }

            mFull.offer(rgb);
        }

        protected void onRun() {
            ShortBuffer buf = null;
            while (mRunning) {
                try {
                    buf = mFull.poll(100, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    Trace.T("error");
                }

                if (buf != null) {
                    showImage(buf);
                }
            }
        }

        private void addEmpty(ShortBuffer buf) {
            synchronized (mEmpty) {
                mEmpty.add(buf);
            }
        }

        private void showImage(ShortBuffer buf) {
            buf.rewind();
            mBmp.copyPixelsFromBuffer(buf);
            addEmpty(buf);
            drawOnSurface(mBmp);
        }

        private void drawOnSurface(Bitmap bmp) {
            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            //图像显示时的图片翻转
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mBmp = rotateBitmap(mBmp, 180);
            }
            canvas.drawBitmap(mBmp, mBmpRC, mSurfaceRC, null);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

        ShortBuffer getEmpty() {
            synchronized (mEmpty) {
                if (!mEmpty.isEmpty()) {
                    return mEmpty.remove(0);
                }
            }
            if (mAlloced >= mMaxAlloc) {
                Trace.T("error " + mAlloced);
                return null;
            }
            Trace.T("alloc");
            mAlloced++;
            return ShortBuffer.allocate(mWidth * mHeight);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mImgRefresh.onSurfaceChanged();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Trace.T();
        mSurfaceHolder = holder;
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
        startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        Trace.T();
        mWriter.closePreview();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
        if (mImgRefresh != null) {
            mImgRefresh.stop();
        }
    }

    public static int getTimeStampInNsFromSampleCounted(int paramInt) {
        return (int) (paramInt / 0.0441D);
    }

    //语音相关
    public class MP4AudioRecorder implements Runnable {
        private int sampleRate = 44100;
        int bufferSize;
        short[] audioData;
        int bufferReadResult;
        private AudioRecord audioRecord;
        public volatile boolean isInitialized;
        private int mCount = 0;
        private Thread mThread;
        private boolean mRun = false;

        public void start() {
            if (mRun) {
                Trace.T("cancel rep");
                return;
            }

            Trace.T();
            mRun = true;
            mAudioTimestamp = 0;
            mCount = 0;

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            mThread = new Thread(this);
            mThread.start();
        }

        public void stop() {
            mRun = false;
            if (mThread == null) {
                Trace.T("cancel rep");
                return;
            }

            try {
                Trace.T("waiting stop");
                mThread.join();
            } catch (Exception e) {
            }
            mThread = null;
            Trace.T("stopped");
        }

        private MP4AudioRecorder() {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize % 1024 != 0) {
                bufferSize = ((bufferSize / 1024) + 1) * 1024;
            }

            audioData = new short[bufferSize];
        }

        private void record(short[] data, int offset, int len) {
            mCount += len;
            mWriter.addAudio(data, offset, len, 0);
        }

        private void updateTimestamp() {
            int i = getTimeStampInNsFromSampleCounted(this.mCount);
            if (mAudioTimestamp != i) {
                mAudioTimestamp = i;
                mAudioTimeRecorded = System.nanoTime();
            }
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (this.audioRecord.getState() == 0) {
                try {
                    Trace.T("audiorecorder waitting");
                    Thread.sleep(100L);
                } catch (InterruptedException localInterruptedException) {
                }
            }

            isInitialized = true;
            audioRecord.startRecording();

            while ((mRun /*|| mVideoTimestamp > mAudioTimestamp*/)
                    && (mAudioTimestamp < 1000 * mMaxVideoDurationInMs)) {
                updateTimestamp();
                bufferReadResult = audioRecord.read(audioData, 0, audioData.length);

                if (bufferReadResult > 0 && !isRecordingStarted && mRun) {
                    initMP4Params(mParams);            // 开启视频相关录制
                }

                if (bufferReadResult < 0) {
                    Trace.T("error");
                    break;
                }

                Trace.T("audiorecorder read " + bufferReadResult + " " + mVideoTimestamp + " " + mAudioTimestamp);
                if ((bufferReadResult > 0) && (mRun /*|| (mVideoTimestamp > mAudioTimestamp)*/))
                    record(audioData, 0, bufferReadResult);
            }
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            Trace.T();

            if (mAudioTimestamp >= 1000 * mMaxVideoDurationInMs) {
                notifyTimeout();
            }
        }
    }

    private static class RecordParams {
        int AudioBitrate;            // 128000 高 96000 低
        int AudioQuality;            // 0 高  5中  20低
        int AudioSampleRate;        // 采样率

        int VideoFrameRate;            // 帧率
        int VideoQuality;            // 0 高  5中  20低
        int VideoBitrate;            // 1000000
    }

    public static interface MP4RecorderDelegate {
        public void onTimeout(String path);

        public void onFailed(int err);

        public void onCanceled();

        public void onFinished(String path);

        public void onStarted();
    }

    private void notifyTimeout() {
        Trace.T();
        mHandler.post(new Runnable() {
            public void run() {
                MP4RecorderDelegate dele = mDelegate;
                if (dele == null) {
                    return;
                }
                dele.onTimeout(mOutputFileName);
            }
        });
    }

    private void onCanceled() {
        Trace.T();
        mHandler.post(new Runnable() {
            public void run() {
                MP4RecorderDelegate dele = mDelegate;
                if (dele == null) {
                    return;
                }
                dele.onCanceled();
                mDelegate = null;
            }
        });
    }

    private void onFinished() {
        Trace.T();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                MP4RecorderDelegate dele = mDelegate;
                if (dele == null) {
                    return;
                }
                dele.onFinished(mOutputFileName);
                mDelegate = null;
                startPreview();
            }
        }, 200);
    }

    private void onStarted() {
        Trace.T();
        mHandler.post(new Runnable() {
            public void run() {
                MP4RecorderDelegate dele = mDelegate;
                if (dele == null) {
                    return;
                }
                dele.onStarted();
            }
        });
    }

    //实现函数：旋转图片
    public Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            matrix.postScale(-1, 1);
            try {
                Bitmap tempBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                if (bitmap != tempBm) {
                    // bitmap回收
                    bitmap.recycle();
                    bitmap = tempBm;
                }
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        return bitmap;

    }

    public static void rotateYUV240SP(byte[] src, byte[] des, int width, int height) {

        int wh = width * height;
        //旋转Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                des[k] = src[width * j + i];
                k++;
            }
        }

        for (int i = 0; i < width; i += 2) {
            for (int j = 0; j < height / 2; j++) {
                des[k] = src[wh + width * j + i];
                des[k + 1] = src[wh + width * j + i + 1];
                k += 2;
            }
        }

    }
}