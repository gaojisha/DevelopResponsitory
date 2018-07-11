package com.gjs.developresponsity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.video.activity.FullVideoPlayActivity;
import com.gjs.developresponsity.video.activity.VideoPlayActivity;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnRetryableFileDownloadStatusListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_COMPLETED;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/06/15
 *     desc    : 视频列表适配器
 *     version : 1.0
 * </pre>
 */
public class RecycleVideoAdapter extends RecyclerView.Adapter implements OnRetryableFileDownloadStatusListener {

    private Context mContext;
    private List<DownloadFileInfo> mVideoList = new ArrayList<>();

    public void setVideoList(Context context, List<DownloadFileInfo> videoList) {
        this.mContext = context;
        this.mVideoList = videoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item_view = LayoutInflater.from(mContext).inflate(R.layout.item_video,parent,false);
        VideoViewHolder videoHolder = new VideoViewHolder(item_view);
        return videoHolder;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        VideoViewHolder videoHolder = (VideoViewHolder)holder;
        final DownloadFileInfo item = mVideoList.get(position);
//        FileDownloader.start(item.getVideoUrl());
//        Glide.with(mContext).load(item.getVideoCover()).into(videoHolder.cover);
        videoHolder.tvTitle.setText(String.valueOf(item.getId()));
        videoHolder.tvDescript.setText(String.valueOf(item.getStatus()));
        switch (item.getStatus()){
            case Status.DOWNLOAD_STATUS_PREPARED://准备下载
            case Status.DOWNLOAD_STATUS_PREPARING://准备下载
                videoHolder.tvStatus.setText(R.string.video_prepare);
                break;
            case Status.DOWNLOAD_STATUS_WAITING://等待下载
                videoHolder.tvStatus.setText(R.string.video_waitting);
                break;
            case Status.DOWNLOAD_STATUS_DOWNLOADING://下载中
                videoHolder.tvStatus.setText(R.string.video_downloading);
                int progress = (mVideoList.get(position).getDownloadedSize())/(mVideoList.get(position).getFileSize()) * 100;
                videoHolder.tvStatus.setText(mContext.getString(R.string.video_downloading) + String.format(mContext.getString(R.string.down_progress),progress));
                break;
            case DOWNLOAD_STATUS_COMPLETED://下载完成
                videoHolder.tvStatus.setText(R.string.video_complete);
                break;
            case Status.DOWNLOAD_STATUS_FILE_NOT_EXIST://下载失败，文件找不到
                videoHolder.tvStatus.setText(R.string.video_file_not_exist);
                break;
            case Status.DOWNLOAD_STATUS_ERROR://下载错误
                videoHolder.tvStatus.setText(R.string.video_error);
                break;
            case Status.DOWNLOAD_STATUS_RETRYING://正在重试
                videoHolder.tvStatus.setText(R.string.video_retry);
                break;
            case Status.DOWNLOAD_STATUS_PAUSED://已暂停
                videoHolder.tvStatus.setText(R.string.video_paused);
                break;
            case Status.DOWNLOAD_STATUS_UNKNOWN://发生未知错误
                videoHolder.tvStatus.setText(R.string.video_unknown);
                break;
            default:
                videoHolder.tvStatus.setText(R.string.video_unknown);
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(mVideoList != null) {
            return mVideoList.size();
        }
        return 0;
    }

    public void pauseAll(){
        FileDownloader.pauseAll();
    }

    public void startAll(){
        for(int i = 0; i<mVideoList.size(); i++) {
            FileDownloader.start(mVideoList.get(i).getUrl());
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView tvTitle;
        TextView tvDescript;
        TextView tvStatus;
        ProgressBar progressBar;

        public VideoViewHolder(View view){
            super(view);
            cover = view.findViewById(R.id.recycler_video_cover);
            tvTitle = view.findViewById(R.id.recycler_video_title);
            tvDescript = view.findViewById(R.id.recycler_video_descript);
            tvStatus = view.findViewById(R.id.recycler_is_downing);
            progressBar = view.findViewById(R.id.recycler_video_downProgress);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new File(mVideoList.get(getAdapterPosition()).getFilePath()).exists()&&FileDownloader.getDownloadFile(mVideoList.get(getAdapterPosition()).getUrl()).getStatus() == DOWNLOAD_STATUS_COMPLETED) {
//                        Intent intent = new Intent(mContext, VideoPlayActivity.class);
                        Intent intent = new Intent(mContext, FullVideoPlayActivity.class);
                        intent.putExtra("videourl", "url：" + mVideoList.get(getAdapterPosition()).getUrl() + " position ： " + getAdapterPosition());
                        intent.putExtra("videopath", mVideoList.get(getAdapterPosition()).getFilePath());
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "未下载完成", Toast.LENGTH_SHORT).show();
                        FileDownloader.start(mVideoList.get(getAdapterPosition()).getUrl());
                    }
                }
            });

            cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"index : " + mVideoList.get(getAdapterPosition()).getFileName(), Toast.LENGTH_SHORT).show();
                    FileDownloader.start(mVideoList.get(getAdapterPosition()).getUrl());
                }
            });
        }
    }

    @Override
    public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
        // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
    }

    @Override
    public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
        // 等待下载（等待其它任务执行完成，或者FileDownloader在忙别的操作）
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
        // 准备中（即，正在连接资源）
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
        // 已准备好（即，已经连接到了资源）
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
        // 正在下载，downloadSpeed为当前下载速度，单位KB/s，remainingTime为预估的剩余时间，单位秒
        android.util.Log.i("gaojishatest", "download Speed : " + downloadSpeed + "KB/S");
        android.util.Log.i("gaojishatest", "downloadInfo : " + downloadFileInfo);
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
        // 下载已被暂停
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
        // 下载完成（整个文件已经全部下载完成）
        refreshCurrent();
    }

    @Override
    public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
        // 下载失败了，详细查看失败原因failReason，有些失败原因你可能必须关心

        String failType = failReason.getType();
        String failUrl = failReason.getUrl();// 或：failUrl = url，url和failReason.getUrl()会是一样的

        if(FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failType)){
            // 下载failUrl时出现url错误x
        }else if(FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failType)){
            // 下载failUrl时出现本地存储空间不足
        }else if(FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failType)){
            // 下载failUrl时出现无法访问网络
        }else if(FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failType)){
            // 下载failUrl时出现连接超时
        }else{
            // 更多错误....
        }

        // 查看详细异常信息
        Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()

        // 查看异常描述信息
        String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
    }

    public void refreshCurrent(){
        mVideoList = FileDownloader.getDownloadFiles();
        notifyDataSetChanged();
    }

}
