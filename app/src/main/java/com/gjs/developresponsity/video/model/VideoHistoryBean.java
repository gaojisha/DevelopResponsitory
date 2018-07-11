package com.gjs.developresponsity.video.model;

/**
 * 历史记录
 * Created by huangchao on 2016/10/1.
 */
public class VideoHistoryBean {

    /**视频名称**/
    private String videoName;

    /**是否是编辑状态**/
    private boolean isEditState;

    /**是否为录制**/
    private boolean isRec;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public boolean isEditState() {
        return isEditState;
    }

    public void setEditState(boolean editState) {
        isEditState = editState;
    }

    public boolean isRec() {
        return isRec;
    }

    public void setRec(boolean rec) {
        isRec = rec;
    }



    @Override
    public String toString() {
        return "VideoHistoryBean{" +
                "videoName='" + videoName + '\'' +
                ", isEditState=" + isEditState +
                ", isRec=" + isRec +
                '}';
    }
}
