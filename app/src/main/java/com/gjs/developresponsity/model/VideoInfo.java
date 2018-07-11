package com.gjs.developresponsity.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/06/15
 *     desc    : Video信息保存
 *     version : 1.0
 * </pre>
 */
@Entity
public class VideoInfo {
    @Id
    private Long id;
    private String videoCover;
    private String videoTitle;
    private String videoUrl;
    private String videoPath;
    private String videoDescript;

    public VideoInfo(String videoCover, String videoTitle, String videoUrl,
            String videoDescript,String videoPath) {
        this.videoCover = videoCover;
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.videoPath = videoPath;
        this.videoDescript = videoDescript;
    }

    @Generated(hash = 1255610469)
    public VideoInfo(Long id, String videoCover, String videoTitle, String videoUrl,
            String videoPath, String videoDescript) {
        this.id = id;
        this.videoCover = videoCover;
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
        this.videoPath = videoPath;
        this.videoDescript = videoDescript;
    }

    @Generated(hash = 296402066)
    public VideoInfo() {
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescript() {
        return videoDescript;
    }

    public void setVideoDescript(String videoDescript) {
        this.videoDescript = videoDescript;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
