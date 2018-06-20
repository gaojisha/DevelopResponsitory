package com.gjs.developresponsity.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.model.VideoInfo;

import org.wlf.filedownloader.DownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.util.ArrayList;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/06/15
 *     desc    : 视频列表适配器
 *     version : 1.0
 * </pre>
 */
public class RecycleVideoAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<VideoInfo> mVideoList = new ArrayList<>();

    public void setVideoList(Context context,ArrayList<VideoInfo> videoList) {
        this.mContext = context;
        this.mVideoList = videoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item_view = LayoutInflater.from(mContext).inflate(R.layout.item_video,null);
        VideoViewHolder videoHolder = new VideoViewHolder(item_view);
        return videoHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder videoHolder = (VideoViewHolder)holder;
        final VideoInfo item = mVideoList.get(position);
        Glide.with(mContext).load(item.getVideoCover()).into(videoHolder.cover);
        videoHolder.tvTitle.setText(item.getVideoTitle());
        videoHolder.tvDescript.setText(item.getVideoDescript());
        videoHolder.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DownloadConfiguration.
                FileDownloader.start(item.getVideoUrl());
            }
        });

//        MediaStore.Video.Thumbnails.getThumbnail(ContentResolver.g)
//        videoHolder.tvProgress.setText("");
//        videoHolder.tvProgress;
    }

    @Override
    public int getItemCount() {
        if(mVideoList != null) {
            return mVideoList.size();
        }
        return 0;
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView tvTitle;
        TextView tvDescript;
        TextView tvProgress;
        ProgressBar progressBar;

        public VideoViewHolder(View view){
            super(view);
            cover = view.findViewById(R.id.recycler_video_cover);
            tvTitle = view.findViewById(R.id.recycler_video_title);
            tvDescript = view.findViewById(R.id.recycler_video_descript);
//            tvProgress = view.findViewById(R.id.recycler_video_progress);
            progressBar = view.findViewById(R.id.recycler_video_downProgress);
        }
    }



}
