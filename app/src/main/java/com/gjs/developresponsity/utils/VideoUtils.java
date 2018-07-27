package com.gjs.developresponsity.utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.video.adapter.VideoHistoryAdapter;
import com.gjs.developresponsity.video.model.VideoHistoryBean;
import com.gjs.developresponsity.video.view.GridViewWithHeaderAndFooter;
import com.gjs.developresponsity.video.view.TextureVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频相关工具类
 */
public class VideoUtils {
    public static final String SUFFIX_IMAGE = "_thumb.jpg";
    public static final String SUFFIX_VIDEO = ".mp4";
    /**
     * 录制的基路径
     **/
    private static String RECORD_VIDEO_BASE_PATH = Environment.getExternalStorageDirectory() + "/icbcim/";

    /**
     * 录制的视频路径
     *
     * @return
     */
    public static String getRecordVideoPath() {
        return RECORD_VIDEO_BASE_PATH + "video/"
//                + IMApp.getInstance().getContactManager().getSelfCard().getUser_id() + "/";
                + "gaojisha" + "/";
    }

    /**
     * 录制的首帧图路径
     **/
    public static String getRecordImagePath() {
        return RECORD_VIDEO_BASE_PATH + "image/"
//                + IMApp.getInstance().getContactManager().getSelfCard().getUser_id() + "/";
                + "gaojisha" + "/";
    }

    /**
     * 首帧图格式
     **/
    public static String RECORDE_VIDEO_IMAGE_FORMAT = ".jpg";

    /**
     * 上传和下载缩略图的路径
     **/
    public static String getUploadImagePath() {
        return DirectoryBuilder.DIR_IMAGE +
                "gaojisha" + File.separator;
//                String.valueOf(IMApp.getInstance().getContactManager().getSelfCard().getUser_id()) + File.separator;
    }

    /**
     * 上传和下载视频的路径
     **/
    public static String getUploadVideoPath() {

        File file = new File(DirectoryBuilder.FUNNY_VIDEO_DIR +
               "gaojisha");
//                String.valueOf(IMApp.getInstance().getContactManager().getSelfCard().getUser_id()));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator;
    }

//    public static DisplayImageOptions history_circle_options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.small_video_circle_default)
//            .showImageForEmptyUri(R.drawable.small_video_circle_default)
//            .showImageOnFail(R.drawable.small_video_circle_default)
//            .cacheInMemory(true)
//            .cacheOnDisk(true)
//            .considerExifParams(true)
//            .displayer(new RoundedBitmapDisplayer(12))
//            .build();

//    public static DisplayImageOptions video_options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.small_video_default)
//            .showImageForEmptyUri(R.drawable.small_video_default)
//            .showImageOnFail(R.drawable.small_video_default)
//            .cacheInMemory(true)
//            .cacheOnDisk(true)
//            .considerExifParams(true)
//            .displayer(new SimpleBitmapDisplayer())
//            .build();

    private static long secFormat = 1000;
    private static long minFormat = secFormat * 60;
    private static long hourFormat = minFormat * 60;
    private static long dayFormat = hourFormat * 24;
    private static long twoDayFormat = dayFormat * 2;
    private static long threeDayFormat = dayFormat * 3;

    /**
     * 历史记录最大Video数量
     **/
    public static final int MAX_VIDEO_NUM = 150;


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * dp 2 px
     *
     * @param dpVal
     */
    public static int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 得到手机屏幕的宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }


    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取手机音量
     */
    public static float getSystemVolume(Context context) {
        if (context != null) {
            try {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0F / maxVolume;
            } catch (UnsupportedOperationException e) {

            }
        }
        return 0.5F;
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
        File pathFile = new File(path);
        return pathFile.exists() && pathFile.isFile();
    }

    /**
     * 判断手机网络情况
     *
     * @param context
     * @return
     */
    public static int phoneNetType(Context context) {
        ConnectivityManager comMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = comMan.getActiveNetworkInfo();
        return networkInfo.getType();
    }


    /**
     * 变大
     *
     * @param context
     * @param view
     */
    public static void scaleBigAnimation(Context context, View view) {
        Log.v("hcTest", "hcTest big");
        AnimationSet bigAnimationSet = new AnimationSet(true);
        //由于部分手机变大以后出现问题，所以采用属性动画放大
//        Animation bigScaleAnimation = AnimationUtils.loadAnimation(context, R.anim.history_scale_big_anim);
//        bigAnimationSet.addAnimation(bigScaleAnimation);
        bigAnimationSet.setFillAfter(true);
        view.startAnimation(bigAnimationSet);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1.1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1.1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY).setDuration(500).start();

    }

    /**
     * 变小
     *
     * @param context
     * @param view
     */
    public static void scaleSmallAnimation(Context context, View view) {
        /**必须该view判断是否之前发生过变化**/
        if (view.getAnimation() != null && view.getAnimation().getFillAfter()) {
            Log.v("hcTest", "hcTest small");
            AnimationSet smallAnimationSet = new AnimationSet(true);
//            Animation smallScaleAnimation = AnimationUtils.loadAnimation(context, R.anim.history_scale_small_anim);
//            smallAnimationSet.addAnimation(smallScaleAnimation);
            smallAnimationSet.setFillAfter(true);
            view.startAnimation(smallAnimationSet);
            view.setAnimation(null);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1.1f, 1f, 1f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1f, 1f);
            ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY).setDuration(500).start();
        }
    }


    /**
     * 距当前时间时间差
     *
     * @param timeStamp 时间戳
     * @return
     */
    public static String durationToCurrent(long timeStamp) {
        //当前时间
        long currentTime = System.currentTimeMillis();
        // 得到毫秒值
        long timeDiff = currentTime - timeStamp;
        if (timeDiff < 0) {
            timeDiff = Math.abs(timeDiff);
        }
        if (0 <= timeDiff && timeDiff < minFormat) {
            return "刚刚";
        } else if (minFormat <= timeDiff && timeDiff < hourFormat) {
            return (timeDiff / minFormat) + "分钟前";
        } else if (hourFormat <= timeDiff && timeDiff < dayFormat) {
            return (timeDiff / hourFormat) + "小时前";
        } else if (dayFormat <= timeDiff && timeDiff < twoDayFormat) {
            return "昨天";
        } else if (twoDayFormat <= timeDiff && timeDiff < threeDayFormat) {
            return "前天";
        } else {
            return (timeDiff / dayFormat) + "天前";
        }
    }

    /**
     * 根据时间戳获取当前时间
     *
     * @param timeStamp
     * @return
     */
    public static String timeStampToDate(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 获取年月日
     *
     * @param timeStamp
     * @return
     */
    public static String getYearDateByTimeStamp(long timeStamp) {
        String time = timeStampToDate(timeStamp);
        String date = time.substring(0, 11);
        return date;
    }


    /**
     * 获取月日
     *
     * @param timeStamp
     * @return
     */
    public static String getDateByTimeStamp(long timeStamp) {
        String time = timeStampToDate(timeStamp);
        String date = time.substring(5, 11);
        return date;
    }


    /**
     * 判断是否是7天前
     *
     * @param timeStamp 时间戳
     * @return 返回true表示7天前，返回false表示7天内
     */
    public static boolean isBefore7Day(String timeStamp) {
        //必须全为数字
        if (!TextUtils.isEmpty(timeStamp) && timeStamp.matches("^[0-9]*$")) {
            long time = Long.parseLong(timeStamp);
            //当前时间
            long currentTime = System.currentTimeMillis();
            // 得到毫秒值
            long timeDiff = currentTime - time;
            if ((double) (timeDiff / dayFormat) > 7) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * 获取文件夹下7天内的mp4文件的名称
     *
     * @param fileAbsolutePath 文件夹路径
     * @return
     */
    public static List<String> video7DayFileName(String fileAbsolutePath) {
        List<String> fileList = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        if (file.exists() && file.isDirectory()) {
            File[] subFile = file.listFiles();
            for (int i = 0; i < subFile.length; i++) {
                // 判断是否为文件夹
                if (!subFile[i].isDirectory()) {
                    String fileName = subFile[i].getName();
                    // 判断是否为mp4结尾
                    if (fileName.trim().toLowerCase().endsWith(".mp4")) {
                        String withoutSuffixFileName = fileName.substring(0, fileName.lastIndexOf("."));
                        if (!isBefore7Day(withoutSuffixFileName)) {
                            File videoFile = new File(fileAbsolutePath + fileName);
                            //缩略图的名称
                            String thumbnailName = withoutSuffixFileName + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;
                            File thumbnailFile = new File(VideoUtils.getRecordImagePath() + thumbnailName);
                            //小于5KB，并且对应的视频无缩略图时，删除该视频。(为无法播放的视频)
                            if (videoFile.length() < 5 * 1024 && !thumbnailFile.exists()) {
                                videoFile.delete();
                            } else {
                                fileList.add(fileName);
                            }

                        } else {
                            File videoFile = new File(fileAbsolutePath + fileName);
                            videoFile.delete();
                        }

                    }
                }
            }
        }
        return fileList;
    }

    /**
     * 上限最大为150个
     *
     * @param fileNames
     * @param isEdit    是否为编辑状态
     * @return
     */
    public static List<VideoHistoryBean> showVideoList(List<String> fileNames, boolean isEdit) {
        List<VideoHistoryBean> videoBeans = new ArrayList<VideoHistoryBean>();
        if (fileNames != null && fileNames.size() > 0) {
            //控制在150个最大值
            int totalSize = fileNames.size();
            if (totalSize > MAX_VIDEO_NUM) {
                fileNames = fileNames.subList(fileNames.size() - MAX_VIDEO_NUM, fileNames.size());
//                int moreSize = totalSize - MAX_VIDEO_NUM;
//                for (int j = 0; j < moreSize; j++) {
//                    fileNames.remove(j);
//                }
            }
            if (isEdit) {
                for (int i = 0; i < fileNames.size(); i++) {
                    VideoHistoryBean videoHistoryBean = new VideoHistoryBean();
                    videoHistoryBean.setEditState(true);
                    videoHistoryBean.setRec(false);
                    String suffixFileName = fileNames.get(i);
                    videoHistoryBean.setVideoName(suffixFileName);
                    videoBeans.add(videoHistoryBean);

                }
            } else {
                for (int i = 0; i < fileNames.size(); i++) {
                    VideoHistoryBean videoHistoryBean = new VideoHistoryBean();
                    videoHistoryBean.setEditState(false);
                    videoHistoryBean.setRec(false);
                    String suffixFileName = fileNames.get(i);
                    videoHistoryBean.setVideoName(suffixFileName);
                    videoBeans.add(videoHistoryBean);

                }
            }


        }

        return videoBeans;
    }

    /**
     * 删除历史记录视频和首帧缩略图
     *
     * @param videoPath 视频路径
     */
    public static void deleteVideoAndThumbnail(String videoPath) {
        File deleteFile = new File(videoPath);
        if (deleteFile.exists()) {
            String deleteFileName = deleteFile.getName();
            //缩略图的名称
            String thumbnailName = deleteFileName.substring(0, deleteFileName.lastIndexOf("."))
                    + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;
            File thumbnailFile = new File(VideoUtils.getRecordImagePath() + thumbnailName);
            if (thumbnailFile.exists()) {
                thumbnailFile.delete();
            }
            deleteFile.delete();
        }
    }


    /**
     * 通过videoPath获取缩略图路径
     *
     * @param videoPath
     * @return
     */
    public static String getVideoThumbnailPath(String videoPath) {
        String videoThumbnailPath = null;
        File videoFile = new File(videoPath);
        if (videoFile.exists() && videoFile.isFile()) {
            String videoName = videoFile.getName();
            //缩略图的名称
            String videoThumbnail = videoName.substring(0, videoName.lastIndexOf("."))
                    + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;
            videoThumbnailPath = VideoUtils.getRecordImagePath() + videoThumbnail;
        }
        return videoThumbnailPath;
    }


    /**
     * 淡出动画
     *
     * @param context
     * @param v
     */
    public static void viewFadeOut(Context context, final View v) {
        if (context == null || v == null) {
            return;
        }
        Animation fadeAnim = AnimationUtils.loadAnimation(context,
                R.anim.capture_hint_fade_out);
        fadeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (v != null && v.getVisibility() == View.GONE) {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(fadeAnim);
    }

    public enum CAPTURE_STATE {
        EMPTY,
        RECORDING,
        TRY_STOPPING,
    }


    /**
     * 传入录制小视频路径，获得第一帧图片
     * @param path 小视频路径
     */
    public static String getFirstImagePath(String path) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            retriever.setDataSource(path);
            // 取得视频的长度(单位为毫秒)
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            // 取得视频的长度(单位为秒)
            int seconds = Integer.valueOf(time) / 1000;
            // 得到每一秒时刻的bitmap比如第一秒,第二秒
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap == null) {
                Thread.sleep(200);
                retriever.setDataSource(path);
                bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            }
            String imagePath = getRecordImagePath();
            File file = new File(imagePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File video = new File(path);
            String videoName = video.getName();
            String imageName = videoName.substring(0, videoName.lastIndexOf("."));
            File imageFile = new File(imagePath, imageName + ".jpg");
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (retriever != null) {
                retriever.release();
                retriever = null;
            }
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }

    }


    /**
     * 保存bitmap到指定路径和名称
     *
     * @param fileName 文件名
     * @param path   保存路径
     * @return
     */
    public static String saveBitmap2Path(String path, String fileName) {
        InputStream is = ContextHelper.getContext().getResources().openRawResource(R.raw.image_video_place);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File imageFile = new
                File(path, fileName);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(imageFile);
            byte[] array = new byte[1024];
            int index = is.read(array);
            while (index != -1) {
                os.write(array, 0, index);
                index = is.read(array);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageFile.getPath();
    }

    private static boolean canUseAudio;

    /**
     * 跳转到录制页面前先调用该方法，为了用户第一次录制时提前弹出"本地通话权限和录音权限"
     *
     * @return
     */
    public static boolean audioIsCanUse() {
        if (canUseAudio) {
            return canUseAudio;
        }
        int sampleRate = 44100;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize % 1024 != 0) {
            bufferSize = ((bufferSize / 1024) + 1) * 1024;
        }
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if (audioRecord != null) {
            audioRecord.startRecording();
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                canUseAudio = true;
            }
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        return canUseAudio;
    }

    /**
     * 通过尝试打开相机的方式判断有无拍照权限（在6.0以下使用拥有root权限的管理软件可以管理权限）
     *
     * @return
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }






    /**
     * 将item布局变小
     *
     * @param context
     * @param position
     */
    public static void updateSmallItemView(GridViewWithHeaderAndFooter gridViewWithHeaderAndFooter, Context context, int position) {
        int firstVisiblePosition = gridViewWithHeaderAndFooter.getFirstVisiblePosition();
        if (position - firstVisiblePosition >= 0) {
            View view = gridViewWithHeaderAndFooter.getChildAt(position - firstVisiblePosition);
            if (view != null && view.getTag() != null) {
                VideoHistoryAdapter.ViewHolder viewHolder = (VideoHistoryAdapter.ViewHolder) view.getTag();
                viewHolder.item_recorde_or_thumbnail_layout = (RelativeLayout) view.findViewById(R.id.item_recorde_or_thumbnail_layout);
                viewHolder.history_item_player_layout = (RelativeLayout) view.findViewById(R.id.history_item_player_layout);
                viewHolder.history_item_player = (TextureVideoView) view.findViewById(R.id.history_item_player);
                viewHolder.history_item_layout = (RelativeLayout) view.findViewById(R.id.history_item_layout);
                viewHolder.item_recorde_or_thumbnail_layout.setVisibility(View.VISIBLE);
                viewHolder.history_item_player_layout.setVisibility(View.GONE);
                viewHolder.history_item_player.stop();
                VideoUtils.scaleSmallAnimation(context, viewHolder.history_item_layout);
            }

        }

    }


    /**
     * 将item布局变大
     *
     * @param context
     * @param position
     */
    public static void updateBigItemView(GridViewWithHeaderAndFooter gridViewWithHeaderAndFooter,
                                         List<VideoHistoryBean> historyList, Context context, int position) {
        int firstVisiblePosition = gridViewWithHeaderAndFooter.getFirstVisiblePosition();
        if (position - firstVisiblePosition >= 0) {
            View view = gridViewWithHeaderAndFooter.getChildAt(position - firstVisiblePosition);
            VideoHistoryAdapter.ViewHolder viewHolder = (VideoHistoryAdapter.ViewHolder) view.getTag();
            viewHolder.history_item_player_layout = (RelativeLayout) view.findViewById(R.id.history_item_player_layout);
            viewHolder.item_recorde_or_thumbnail_layout = (RelativeLayout) view.findViewById(R.id.item_recorde_or_thumbnail_layout);
            viewHolder.history_item_player = (TextureVideoView) view.findViewById(R.id.history_item_player);
            viewHolder.history_item_player_layout.setVisibility(View.VISIBLE);
            viewHolder.item_recorde_or_thumbnail_layout.setVisibility(View.GONE);
            viewHolder.history_item_player.setVideoPath(VideoUtils.getRecordVideoPath() + historyList.get(position).getVideoName());
            viewHolder.history_item_player.start();
            viewHolder.history_item_player.setMediaPlayerCallback(new TextureVideoView.MediaPlayerCallback() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }

                @Override
                public void onCompletion(MediaPlayer mp) {

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

            VideoUtils.scaleBigAnimation(context, viewHolder.history_item_layout);
        }
    }
}
