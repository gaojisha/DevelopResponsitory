package com.gjs.developresponsity.video.tools.ffmpeg;

/**
 * Created by Administrator on 2016/5/24.
 */
public class MP4Writer {
    static {
        System.loadLibrary("ffmpeg");
    }

    /**
     * 开启图片变换预览，调用后getRGB565() 可以返回供预览的图片
     *
     * @param srcw
     * @param srch
     * @param dstw
     * @param dsth
     */
    public native void openPreview(int srcw, int srch, int dstw, int dsth, int rorate);

    /**
     * 打开MP4文件作为写入
     *
     * @param path        文件路径
     * @param srcw        原始视频宽度
     * @param srch        原始视频高度
     * @param dstw        最终视频宽度 （截取并旋转）
     * @param dsth        最终视频高度 （截取并旋转）
     * @param vFrameRate  ffmpeg 视频帧率
     * @param vBitrate    ffmpeg 视频带宽 400000
     * @param vQuality    ffmpeg 视频质量 0 高 5 中 20 低
     * @param aBitrate    ffmpeg 音频带宽 128000 高 128000 中 96000 低
     * @param aSampleRate ffmpeg 音频采样率
     * @param aChannels   ffmpeg 音频声道数
     * @param aQuality    ffmpeg 音频质量 0 高 5 中 20 低
     * @return true-打开成功  false-打开失败
     */
    public native boolean openMP4File(String path,                        // 写入文件路径
                                      int srcw, int srch, int dstw, int dsth,                    // 图片尺寸变换
                                      int vFrameRate, int vBitrate, int vQuality,                    // 视频参数
                                      int aBitrate, int aSampleRate, int aChannels, int aQuality, int aRotate);// 音频参数

    /**
     * 写入音频流
     *
     * @param data      数据 pcm16类型
     * @param offset    数据起始位置
     * @param len       数据长度
     * @param timestamp 时间戳
     */
    public native void addAudio(short[] data, int offset, int len, long timestamp);

    /**
     * 写入视频流
     *
     * @param data   数据体
     * @param offset 数据offset
     * @param fmt    格式 1=nv21
     * @param rotate 图片旋转角度 0 或者 90
     */
    public native void addVideo(byte[] data, int offset, int fmt, int rotate, long timestamp);

    /**
     * 关闭MP4文件，完成写入
     *
     * @param stopPreview true-关闭预览输出 false-不关闭预览输出
     */
    public native void closeMP4(boolean stopPreview);

    /**
     * 关闭预览
     */
    public native void closePreview();

    /**
     * 返回经过截取旋转后的图片　格式转化为　RGB565，大小和　openMP4File　传入的dst大小一致
     *
     * @param rgb 返回　addVideo()加入的最后一桢图片
     */
    native public boolean getRGB565(short[] rgb);
}
