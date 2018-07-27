package com.gjs.developresponsity.video.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.FrescoImageUtils;
import com.gjs.developresponsity.utils.VideoUtils;
import com.gjs.developresponsity.video.model.VideoHistoryBean;
import com.gjs.developresponsity.video.tools.NoDoubleClickListener;
import com.gjs.developresponsity.video.view.GridViewWithHeaderAndFooter;
import com.gjs.developresponsity.video.view.NoTitleCornerDialog;
import com.gjs.developresponsity.video.view.TextureVideoView;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 历史记录的Adapter
 */
public class VideoHistoryAdapter extends BaseAdapter {
    private Context context;

    private List<VideoHistoryBean> historyList;

    private int item_width;

    public List<VideoHistoryBean> deleteList;

    private ImageView historyBackIv;

    private TextView historyDeleteCancel;

    private TextView completeOrEdit;

    /**
     * 正在播放的map
     **/
    public Map<Integer, VideoHistoryBean> playMap;

    private String imOrFriend;

    private LinearLayout video_history_layout;

    private GridViewWithHeaderAndFooter videoHistoryGv;

    public VideoHistoryAdapter(Context context, List<VideoHistoryBean> historyList, int item_width, GridViewWithHeaderAndFooter videoHistoryGv,
                               List<VideoHistoryBean> deleteList, ImageView historyBackIv, TextView historyDeleteCancel,
                               TextView completeOrEdit, Map<Integer, VideoHistoryBean> playMap, String imOrFriend, LinearLayout video_history_layout) {
        this.context = context;
        this.historyList = historyList;
        this.item_width = item_width;
        this.videoHistoryGv = videoHistoryGv;
        this.deleteList = deleteList;
        this.historyBackIv = historyBackIv;
        this.historyDeleteCancel = historyDeleteCancel;
        this.completeOrEdit = completeOrEdit;
        this.playMap = playMap;
        this.imOrFriend = imOrFriend;
        this.video_history_layout = video_history_layout;
    }

    @Override
    public int getCount() {
        if (historyList != null && historyList.size() > 0) {
            return historyList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public interface MoreListener {
        void onMoreClickListener();
    }

    private MoreListener moreListener;

    public void setMoreListener(MoreListener moreListener) {
        this.moreListener = moreListener;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.history_video_item, null);
            viewHolder.history_item_player = (TextureVideoView) view.findViewById(R.id.history_item_player);
            viewHolder.history_item_thumbnail = (SimpleDraweeView) view.findViewById(R.id.history_item_thumbnail);
            viewHolder.history_item_layout = (RelativeLayout) view.findViewById(R.id.history_item_layout);
            viewHolder.history_item_recorde_layout = (RelativeLayout) view.findViewById(R.id.history_item_recorde_layout);
            viewHolder.item_recorde_or_thumbnail_layout = (RelativeLayout) view.findViewById(R.id.item_recorde_or_thumbnail_layout);
            viewHolder.history_item_player_layout = (RelativeLayout) view.findViewById(R.id.history_item_player_layout);
            viewHolder.history_item_edit = (ImageView) view.findViewById(R.id.history_item_edit);
            viewHolder.history_item_touch_send = (TextView) view.findViewById(R.id.history_item_touch_send);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RelativeLayout.LayoutParams rLayoutParams = (RelativeLayout.LayoutParams) viewHolder.history_item_layout.getLayoutParams();
        rLayoutParams.width = item_width;
        rLayoutParams.height = item_width * 4 / 5;
        viewHolder.history_item_layout.setLayoutParams(rLayoutParams);

        if (!TextUtils.isEmpty(historyList.get(i).getVideoName())) {

            String thumbnailName = historyList.get(i).getVideoName().
                    substring(0, historyList.get(i).getVideoName().lastIndexOf(".")) + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;
            File thumbnailFile = new File(VideoUtils.getRecordImagePath() + thumbnailName);
            if (thumbnailFile.exists()) {
                //加载首帧图
                FrescoImageUtils.getInstance().createSdcardBuilder("file://" + VideoUtils.getRecordImagePath() + thumbnailName).setPlaceDrawable(R.drawable.small_video_circle_default).build().showSdcardImage( viewHolder.history_item_thumbnail);
            } else {
                FrescoImageUtils.getInstance().createSdcardBuilder("").setPlaceDrawable(R.drawable.small_video_circle_default).build().showSdcardImage( viewHolder.history_item_thumbnail);
            }
        } else {
            FrescoImageUtils.getInstance().createSdcardBuilder("").setPlaceDrawable(R.drawable.small_video_circle_default).build().showSdcardImage( viewHolder.history_item_thumbnail);
        }

        //是否编辑状态
        if (historyList.get(i).isEditState()) {
            viewHolder.history_item_edit.setVisibility(View.VISIBLE);
            viewHolder.history_item_touch_send.setVisibility(View.GONE);
        } else {
            viewHolder.history_item_edit.setVisibility(View.GONE);
            viewHolder.history_item_touch_send.setVisibility(View.VISIBLE);
        }
        viewHolder.history_item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playMap.get(i) != null) {
                    if (deleteList != null && deleteList.size() > 0) {
                        //已经删除过文件了
                        historyDeleteCancel.setText(R.string.history_delete_cancel);
                    } else {
                        historyDeleteCancel.setVisibility(View.GONE);
                        historyBackIv.setVisibility(View.VISIBLE);
                    }

                } else {
                    //删除
                    deleteList.add(historyList.get(i));
                    historyList.remove(i);
                    if (historyDeleteCancel.getVisibility() == View.VISIBLE) {
                        historyDeleteCancel.setText(R.string.history_delete_cancel);
                        if (historyBackIv.getVisibility() == View.VISIBLE) {
                            historyBackIv.setVisibility(View.GONE);
                        }
                    } else {
                        historyDeleteCancel.setVisibility(View.VISIBLE);
                        historyDeleteCancel.setText(R.string.history_delete_cancel);
                        if (historyBackIv.getVisibility() == View.VISIBLE) {
                            historyBackIv.setVisibility(View.GONE);
                        }

                    }

                }
                playMap.clear();
                video_history_layout.setBackgroundColor(context.getResources().getColor(R.color.white));
                if (completeOrEdit.getVisibility() == View.GONE) {
                    completeOrEdit.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        });

        if (playMap.get(i) != null) {
            viewHolder.history_item_player_layout.setVisibility(View.VISIBLE);
            viewHolder.item_recorde_or_thumbnail_layout.setVisibility(View.GONE);
            viewHolder.history_item_player.setVideoPath(VideoUtils.getRecordVideoPath() + historyList.get(i).getVideoName());
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
        } else {
            viewHolder.item_recorde_or_thumbnail_layout.setVisibility(View.VISIBLE);
            viewHolder.history_item_player_layout.setVisibility(View.GONE);
            viewHolder.history_item_player.stop();
            VideoUtils.scaleSmallAnimation(context, viewHolder.history_item_layout);
        }
        //判断是录制还是首帧图
        if (historyList.get(i).isRec()) {
            viewHolder.history_item_thumbnail.setVisibility(View.GONE);
            viewHolder.history_item_recorde_layout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.history_item_thumbnail.setVisibility(View.VISIBLE);
            viewHolder.history_item_recorde_layout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(imOrFriend) && imOrFriend.equals(VideoHistoryFragment.IM)) {
            viewHolder.history_item_touch_send.setText("轻触发送");
        } else {
            viewHolder.history_item_touch_send.setText("轻触选择");
        }
        //整个view点击事件
        view.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if (historyList.get(i).isRec()) {
                    if (historyList.size() - 1 >= VideoUtils.MAX_VIDEO_NUM) {
                        //弹出提示
                        final NoTitleCornerDialog noTitleCornerDialog = new NoTitleCornerDialog(context);
                        noTitleCornerDialog.oneButtonDialog();
                        noTitleCornerDialog.setDialogContent(R.string.video_arrive_max);
                        noTitleCornerDialog.setDialogSureBtnColor(R.color.dialog_red_color);
                        noTitleCornerDialog.setNoTitleCornerDialogCallBack(new NoTitleCornerDialog.NoTitleCornerDialogCallBack() {
                            @Override
                            public void onNoTitleCornerDialogSure() {
                            }

                            @Override
                            public void onNoTitleCornerDialogCancel() {

                            }
                        });
                        noTitleCornerDialog.show();
                    } else {
                        //录制小视频
                        if (!TextUtils.isEmpty(imOrFriend) && imOrFriend.equals(VideoHistoryFragment.IM)) {
                            if (moreListener != null) {
                                moreListener.onMoreClickListener();
                            }
                        } else {
                            //TODO
//                            Intent historyIntent = new Intent(context, SocialCaptureVideoActivity.class);
//                            context.startActivity(historyIntent);

//                            ((Activity) context).finish();此处不能关闭掉该页面
                        }
                    }

                } else {
                    if (playMap.get(i) != null) {
                        if (historyList.get(i).isEditState()) {
                            //取消放大
                            if (historyList.get(0).isEditState()) {
                                //如果之前是编辑状态，因为如果是编辑状态，所有的都是编辑状态，所以只需要获取第0个就行
                                if (deleteList != null && deleteList.size() > 0) {
                                    //已经删除过文件了
                                    historyDeleteCancel.setText(R.string.history_delete_cancel);
                                } else {
                                    historyDeleteCancel.setVisibility(View.GONE);
                                    historyBackIv.setVisibility(View.GONE);
                                }
                            } else {
                                historyDeleteCancel.setVisibility(View.GONE);
                                historyBackIv.setVisibility(View.VISIBLE);
                            }
                            if (completeOrEdit.getVisibility() == View.GONE) {
                                completeOrEdit.setVisibility(View.VISIBLE);
                            }
                            VideoUtils.updateSmallItemView(videoHistoryGv, context, i);
                            playMap.clear();
                            video_history_layout.setBackgroundColor(context.getResources().getColor(R.color.white));
                        } else {
                            if (!TextUtils.isEmpty(imOrFriend) && imOrFriend.equals(VideoHistoryFragment.IM)) {
                                //发布im小视频
                                if (listener != null) {

                                    String videoPath = VideoUtils.getRecordVideoPath() + historyList.get(i).getVideoName();
                                    String thumbnailName = historyList.get(i).getVideoName().
                                            substring(0, historyList.get(i).getVideoName().lastIndexOf(".")) + VideoUtils.RECORDE_VIDEO_IMAGE_FORMAT;

                                    listener.sendVideo(videoPath);
                                }
                            } else {
                                //发布朋友圈小视频
                                String videoPath = VideoUtils.getRecordVideoPath() + historyList.get(i).getVideoName();
                                //TODO
//                                Intent publishIntent = new Intent(context, PublishVideoActivity.class);
//                                publishIntent.putExtra("INTENT_TYPE", 2);
//                                publishIntent.putExtra("VIDEO_PATH", videoPath);
//                                context.startActivity(publishIntent);
//                                ((Activity) context).finish();
                            }
                        }
                    } else {
                        //变化视频尺寸
                        for (int key : playMap.keySet()) {
                            if (playMap.get(key) != null) {
                                VideoUtils.updateSmallItemView(videoHistoryGv, context, key);
                            }
                        }
                        playMap.clear();
//                        notifyDataSetChanged();
                        playMap.put(i, historyList.get(i));
                        VideoUtils.updateBigItemView(videoHistoryGv, historyList, context, i);
                        video_history_layout.setBackgroundColor(context.getResources().getColor(R.color.history_bg_color));
                        if (deleteList != null && deleteList.size() > 0) {
                            //已经删除过文件了
                            historyDeleteCancel.setText(R.string.history_delete_cancel);
                        } else {
                            historyDeleteCancel.setText(R.string.history_scale_cancel);
                        }
                        historyDeleteCancel.setText(R.string.history_scale_cancel);
                        if (historyDeleteCancel.getVisibility() == View.GONE) {
                            historyDeleteCancel.setVisibility(View.VISIBLE);
                        }
                        if (historyBackIv.getVisibility() == View.VISIBLE) {
                            historyBackIv.setVisibility(View.GONE);
                        }

                        if (completeOrEdit.getVisibility() == View.VISIBLE) {
                            completeOrEdit.setVisibility(View.GONE);
                        }
                    }

                }
            }
        });

        return view;

    }

    private VideoHistoryListener listener;

    public VideoHistoryListener getListener() {
        return listener;
    }

    public void setListener(VideoHistoryListener listener) {
        this.listener = listener;
    }

    interface VideoHistoryListener {
        void sendVideo(String videoPath);
    }

    public class ViewHolder {
        /**
         * 播放器
         **/
        public TextureVideoView history_item_player;
        /**
         * 视频第一帧图, 编辑
         **/
        public SimpleDraweeView history_item_thumbnail;
        public ImageView history_item_edit;

        public RelativeLayout history_item_layout, history_item_player_layout,
                history_item_recorde_layout, item_recorde_or_thumbnail_layout;
        //轻触选择
        public TextView history_item_touch_send;
    }

}
