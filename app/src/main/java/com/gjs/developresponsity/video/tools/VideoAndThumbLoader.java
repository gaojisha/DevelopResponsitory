package com.gjs.developresponsity.video.tools;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.FrescoImageUtils;
import com.gjs.developresponsity.utils.VideoUtils;
import com.gjs.developresponsity.video.view.CircleLoading;
import com.gjs.developresponsity.video.view.TextureVideoView;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import java.io.File;
import java.util.HashMap;

public class VideoAndThumbLoader implements OnRetryableFileDownloadStatusListener {
    /**
     * 视频缩略图Tag
     **/
    private static final int TAG_THUMB_KEY_URI = R.id.videoloader_thumb_uri;

    /**
     * 视频Tag
     **/
    private static final int TAG_VIDEO_KEY_URI = R.id.videoloader_video_uri;

    private static VideoAndThumbLoader videoAndThumbLoader = null;

    /**
     * 缩略图下载成功
     **/
    private static final int THUMB_DOWN_SUCCESS = 0;
    /**
     * 缩略图下载失败
     **/
    private static final int THUMB_DOWN_FAIL = 1;

    /**
     * 视频下载成功
     **/
    private static final int VIDEO_DOWN_SUCCESS = 2;

    /**
     * 视频下载过程中
     **/
    private static final int VIDEO_DOWN_DOWNING = 3;

    /**
     * 视频下载失败
     **/
    private static final int VIDEO_DOWN_FAIL = 4;

//    private Cloud cloud = null;

    //私有化构造方法
    private VideoAndThumbLoader() {
    }

    //单例
    public static VideoAndThumbLoader getInstance() {
        if (videoAndThumbLoader == null) {
            synchronized (VideoAndThumbLoader.class) {
                if (videoAndThumbLoader == null) {
                    videoAndThumbLoader = new VideoAndThumbLoader();
                }
            }
        }
        return videoAndThumbLoader;
    }


    private Handler downHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case THUMB_DOWN_SUCCESS:
                    //缩略图
                    ThumbLoaderResult thumbLoaderResult = (ThumbLoaderResult) msg.obj;
                    SimpleDraweeView imageView = thumbLoaderResult.thumbView;
                    String thumbName = (String) imageView.getTag(TAG_THUMB_KEY_URI);
                    if (thumbName.equals(thumbLoaderResult.thumbName)) {
                        if (thumbLoaderResult.thumbPath != null) {
                            FrescoImageUtils.getInstance().createSdcardBuilder("file://" + thumbLoaderResult.thumbPath).setPlaceDrawable(R.drawable.small_video_default).build().showSdcardImage(thumbLoaderResult.thumbView);
                        }
                        setThumbShow(imageView, thumbLoaderResult.videoPath, thumbLoaderResult.textureVideoView);
                    }

                    break;

                case THUMB_DOWN_FAIL:
                    //缩略图下载失败
                    ThumbLoaderResult failResult = (ThumbLoaderResult) msg.obj;
                    SimpleDraweeView failView = failResult.thumbView;
                    String failThumbName = (String) failView.getTag(TAG_THUMB_KEY_URI);
                    if (failThumbName.equals(failResult.thumbName)) {
                        failView.setImageResource(R.drawable.small_video_default);
                        setThumbShow(failView, failResult.videoPath, failResult.textureVideoView);
                    }
                    break;
                case VIDEO_DOWN_SUCCESS:
                    VideoLoaderResult successVideoResult = (VideoLoaderResult) msg.obj;
                    TextureVideoView successVideoView = successVideoResult.textureVideoView;
                    CircleLoading circleLoadingf = successVideoResult.circleLoading;
                    String successFileName = (String) successVideoView.getTag(TAG_VIDEO_KEY_URI);
                    circleLoadingf.setVisibility(View.GONE);
                    circleLoadingf.setStatus(CircleLoading.Status.End);
                    if (successFileName.equals(successVideoResult.videoName)) {
                        playLocalVideo(successVideoResult.circleLoading, successVideoResult.thumbView, successVideoResult.textureVideoView,
                                successVideoResult.videoPath, successVideoResult.videoDownFailLayout);
                    }

                    break;
                case VIDEO_DOWN_DOWNING:
                    VideoLoaderResult loadingVideoResult = (VideoLoaderResult) msg.obj;
                    CircleLoading circleLoading = loadingVideoResult.circleLoading;
                    String loadingName = (String) circleLoading.getTag(TAG_VIDEO_KEY_URI);
                    if (loadingName.equals(loadingVideoResult.videoName)) {
                        loadingVideoResult.videoDownFailLayout.setVisibility(View.GONE);
                        if (loadingVideoResult.videoNowLenght < loadingVideoResult.videoTotalLenght) {
                            if (circleLoading.getVisibility() == View.GONE) {
                                circleLoading.setVisibility(View.VISIBLE);
                                circleLoading.setStatus(CircleLoading.Status.Starting);
                            }
                            //下载过程中更新进度
                            circleLoading.loadAngle(loadingVideoResult.videoNowLenght, loadingVideoResult.videoTotalLenght);
                        }

                    }
                    break;

                case VIDEO_DOWN_FAIL:
                    VideoLoaderResult failVideoResult = (VideoLoaderResult) msg.obj;
                    RelativeLayout videoDownFailLayout = failVideoResult.videoDownFailLayout;
                    String failName = (String) videoDownFailLayout.getTag(TAG_VIDEO_KEY_URI);
                    if (failName.equals(failVideoResult.videoName)) {
                        failVideoResult.thumbView.setVisibility(View.GONE);
                        failVideoResult.circleLoading.setStatus(CircleLoading.Status.End);
                        failVideoResult.circleLoading.setVisibility(View.GONE);

                        if (videoDownFailLayout.getVisibility() == View.GONE) {
                            videoDownFailLayout.setVisibility(View.VISIBLE);
                        }
                        if (failVideoResult.textureVideoView != null && failVideoResult.textureVideoView.isPlaying()) {
                            failVideoResult.textureVideoView.stop();
                        }
                    }
                    break;
            }
        }
    };

//    /**
//     * 加载首帧图
//     */
//    public void thumbLoaderBitmap(final SimpleDraweeView videoThumbnail, final String thumbPath,
//                                  final String videoPath, final TextureVideoView textureVideoView) {
//        final File thumbnailFile = new File(thumbPath);
//        //缩略图名称
//        final String thumbName = thumbnailFile.getName();
//        videoThumbnail.setTag(TAG_THUMB_KEY_URI, thumbName);
//        if (textureVideoView != null) {
//            textureVideoView.setTag(TAG_THUMB_KEY_URI, thumbName);
//        }
//
//        Bitmap bitmap = loadBitmapFromLocal(thumbPath);
//        if (bitmap != null) {
//            if (videoThumbnail.getTag(TAG_THUMB_KEY_URI).equals(thumbName)) {
//                FrescoImageUtils.getInstance().createSdcardBuilder("file://" + thumbPath).setPlaceDrawable(R.drawable.small_video_default).build().showSdcardImage(videoThumbnail);
//                setThumbShow(videoThumbnail, videoPath, textureVideoView);
//                return;
//            }
//        }
//
//
//        File thumbnailPathFile = thumbnailFile.getParentFile();
//        if (!thumbnailPathFile.exists() || !thumbnailPathFile.isDirectory()) {
//            thumbnailPathFile.mkdirs();
//        }
//        CloudRequest thumbnailRequest = new CloudRequest(CloudRequest.Request.DOWNLOAD,
//                CloudRequest.Group.SAVE, thumbnailFile.getName(), thumbnailPathFile.getPath());
//        cloud.intoCloud(thumbnailRequest, new CloudEvent() {
//            @Override
//            public void cloudOK(CloudRequest request) {
//                Bitmap bitmap = loadBitmapFromLocal(thumbPath);
//                if (bitmap != null) {
//                    ThumbLoaderResult thumbLoaderResult = new ThumbLoaderResult(videoThumbnail, thumbName, thumbPath, videoPath, textureVideoView);
//                    downHandler.obtainMessage(THUMB_DOWN_SUCCESS, thumbLoaderResult).sendToTarget();
//                }
//            }
//
//            @Override
//            public void cloudFailed(int status, CloudRequest request) {
//                ThumbLoaderResult thumbLoaderResult = new ThumbLoaderResult(videoThumbnail, thumbName, null, videoPath, textureVideoView);
//                downHandler.obtainMessage(THUMB_DOWN_FAIL, thumbLoaderResult).sendToTarget();
//            }
//
//            @Override
//            public void chipOK(CloudRequest request) {
//
//            }
//
//            @Override
//            public void cloudPause(int status, CloudRequest request) {
//
//            }
//        });
//    }

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

    public void stopDownload() {
        FileDownloader.pauseAll();
    }


    public HashMap<String, VideoLoaderResult> mHashMap = new HashMap<String, VideoLoaderResult>();


    public void fullVideoLoader(final TextureVideoView textureVideoView, final String videoPath,final String videoUrl, final SimpleDraweeView thumbView,
                                final CircleLoading circleLoading, final RelativeLayout videoDownFailLayout) {
        final File videoFile = new File(videoPath);
        //视频名称
        final String videoName = videoFile.getName();
        textureVideoView.setTag(TAG_VIDEO_KEY_URI, videoName);
        thumbView.setTag(TAG_VIDEO_KEY_URI, videoName);
        circleLoading.setTag(TAG_VIDEO_KEY_URI, videoName);
        circleLoading.setVisibility(View.GONE);
        thumbView.setVisibility(View.VISIBLE);
        videoDownFailLayout.setTag(TAG_VIDEO_KEY_URI, videoName);

        if (VideoUtils.fileExists(videoPath)) {
            if (textureVideoView.getTag(TAG_VIDEO_KEY_URI).equals(videoName)) {
                VideoLoaderResult videoLoaderResult = new VideoLoaderResult(textureVideoView, videoPath, thumbView,
                        circleLoading, videoFile.length(), videoFile.length(), videoName, videoDownFailLayout);

                downHandler.obtainMessage(VIDEO_DOWN_SUCCESS, videoLoaderResult).sendToTarget();
                return;
            }
        }

        File videoPathFile = videoFile.getParentFile();
        if (!videoPathFile.exists() || !videoPathFile.isDirectory()) {
            videoPathFile.mkdirs();
        }
        VideoLoaderResult videoLoaderResult = new VideoLoaderResult(textureVideoView, videoPath, thumbView,
                circleLoading, videoFile.length(), videoFile.length(), videoName, videoDownFailLayout);
        if (mHashMap.containsKey(videoFile.getName())) {
            mHashMap.put(videoFile.getName(), videoLoaderResult);
            return;
        } else {
            mHashMap.put(videoFile.getName(), videoLoaderResult);
        }

        FileDownloader.start(videoUrl);
//        CloudRequest videoRequest = new CloudRequest(CloudRequest.Request.DOWNLOAD,
//                CloudRequest.Group.SAVE, videoFile.getName(), videoPathFile.getPath());
//        cloud.intoCloud(videoRequest, new CloudEvent() {
//            @Override
//            public void cloudOK(CloudRequest request) {
////                VideoLoaderResult videoLoaderResult = new VideoLoaderResult(textureVideoView,
////                        videoPath, thumbView, circleLoading, request.getNowLength(), request.getLength(), videoName,
////                        videoDownFailLayout);
//                VideoLoaderResult videoLoaderResult = mHashMap.remove(request.getFilename());
//                if (videoLoaderResult != null) {
//                    videoLoaderResult.videoNowLenght = request.getNowLength();
//                    videoLoaderResult.videoTotalLenght = request.getLength();
//                    downHandler.obtainMessage(VIDEO_DOWN_SUCCESS, videoLoaderResult).sendToTarget();
//                }
//            }
//
//            @Override
//            public void cloudFailed(int status, CloudRequest request) {
////                VideoLoaderResult videoLoaderResult = new VideoLoaderResult(textureVideoView,
////                        videoPath, thumbView, circleLoading, request.getNowLength(), request.getLength(), videoName,
////                        videoDownFailLayout);
//                VideoLoaderResult videoLoaderResult = mHashMap.remove(request.getFilename());
//                if (videoLoaderResult != null) {
//                    videoLoaderResult.videoNowLenght = request.getNowLength();
//                    videoLoaderResult.videoTotalLenght = request.getLength();
//                    downHandler.obtainMessage(VIDEO_DOWN_FAIL, videoLoaderResult).sendToTarget();
//                }
//            }
//
//            @Override
//            public void chipOK(CloudRequest request) {
////                VideoLoaderResult videoLoaderResult = new VideoLoaderResult(textureVideoView,
////                        videoPath, thumbView, circleLoading, request.getNowLength(), request.getLength(), videoName,
////                        videoDownFailLayout);
//                VideoLoaderResult videoLoaderResult = mHashMap.get(request.getFilename());
//                if (videoLoaderResult != null) {
//                    videoLoaderResult.videoNowLenght = request.getNowLength();
//                    videoLoaderResult.videoTotalLenght = request.getLength();
//                    downHandler.obtainMessage(VIDEO_DOWN_DOWNING, videoLoaderResult).sendToTarget();
//                }
//            }
//
//            @Override
//            public void cloudPause(int status, CloudRequest request) {
//            }
//        });
    }


    private void playLocalVideo(CircleLoading circleLoading, final ImageView thumbView, final TextureVideoView textureVideoView,
                                final String localPath, RelativeLayout videoDownFailLayout) {

        circleLoading.setStatus(CircleLoading.Status.End);
        circleLoading.setVisibility(View.GONE);

        if (videoDownFailLayout.getVisibility() == View.VISIBLE) {
            videoDownFailLayout.setVisibility(View.GONE);
        }
        textureVideoView.setVisibility(View.VISIBLE);
        textureVideoView.setVideoPath(localPath);
        textureVideoView.start();
//        if(thumbView.getVisibility() == View.VISIBLE){
//            downHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    thumbView.setVisibility(View.GONE);
//                }
//            },600);
//        }
    }

    /**
     * 加载本地缩略图
     *
     * @param thumbPath
     * @return
     */
    private Bitmap loadBitmapFromLocal(String thumbPath) {
        Bitmap bitmap = null;
        File file = new File(thumbPath);
        if (file.exists() && file.isFile()) {
            bitmap = BitmapFactory.decodeFile(thumbPath);
        }
        return bitmap;
    }

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {

    }

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
        VideoLoaderResult videoLoaderResult = mHashMap.get(downloadFileInfo.getFileName());
        if (videoLoaderResult != null) {
            videoLoaderResult.videoNowLenght = downloadFileInfo.getDownloadedSizeLong();
            videoLoaderResult.videoTotalLenght = downloadFileInfo.getFileSizeLong();
            downHandler.obtainMessage(VIDEO_DOWN_DOWNING, videoLoaderResult).sendToTarget();
        }
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        VideoLoaderResult videoLoaderResult = mHashMap.remove(downloadFileInfo.getFileName());
        if (videoLoaderResult != null) {
            videoLoaderResult.videoNowLenght = downloadFileInfo.getDownloadedSizeLong();
            videoLoaderResult.videoTotalLenght = downloadFileInfo.getFileSizeLong();
            downHandler.obtainMessage(VIDEO_DOWN_SUCCESS, videoLoaderResult).sendToTarget();
        }
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        VideoLoaderResult videoLoaderResult = mHashMap.remove(downloadFileInfo.getFileName());
        if (videoLoaderResult != null) {
            videoLoaderResult.videoNowLenght = downloadFileInfo.getDownloadedSizeLong();
            videoLoaderResult.videoTotalLenght = downloadFileInfo.getFileSizeLong();
            downHandler.obtainMessage(VIDEO_DOWN_FAIL, videoLoaderResult).sendToTarget();
        }
    }


    /**
     * 缩略图结果
     **/
    private class ThumbLoaderResult {
        SimpleDraweeView thumbView;
        String thumbName;
        String thumbPath;
        String videoPath;
        TextureVideoView textureVideoView;

        public ThumbLoaderResult(SimpleDraweeView thumbView, String thumbName, String thumbPath,
                                 String videoPath, TextureVideoView textureVideoView) {
            this.thumbView = thumbView;
            this.thumbName = thumbName;
            this.thumbPath = thumbPath;
            this.videoPath = videoPath;
            this.textureVideoView = textureVideoView;
        }

    }

    private class VideoLoaderResult {
        TextureVideoView textureVideoView;
        String videoPath;
        ImageView thumbView;
        CircleLoading circleLoading;
        long videoNowLenght;
        long videoTotalLenght;
        String videoName;
        RelativeLayout videoDownFailLayout;

        public VideoLoaderResult(TextureVideoView textureVideoView, String videoPath, ImageView thumbView,
                                 CircleLoading circleLoading, long videoNowLenght, long videoTotalLenght,
                                 String videoName, RelativeLayout videoDownFailLayout) {
            this.textureVideoView = textureVideoView;
            this.videoPath = videoPath;
            this.thumbView = thumbView;
            this.circleLoading = circleLoading;
            this.videoNowLenght = videoNowLenght;
            this.videoTotalLenght = videoTotalLenght;
            this.videoName = videoName;
            this.videoDownFailLayout = videoDownFailLayout;
        }
    }
}


