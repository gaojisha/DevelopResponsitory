package com.gjs.developresponsity.video.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.VideoUtils;
import com.gjs.developresponsity.video.model.VideoHistoryBean;
import com.gjs.developresponsity.video.view.GridViewWithHeaderAndFooter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频历史记录fragment
 */
public class VideoHistoryFragment extends BaseFragment implements View.OnClickListener {

    public static final String FROM = "from";
    public static final String IM = "im";
    private String imOrFriend = "";

    /**
     * 展现历史记录的网格
     **/
    private GridViewWithHeaderAndFooter videoHistoryGv;

    private List<VideoHistoryBean> historyList;

    private VideoHistoryAdapter videoHistoryAdapter;

    /**
     * 返回或者撤销删除的layout，用来注册点击事件
     **/
    private RelativeLayout historyBackLayout;
    /**
     * 返回imageView
     **/
    private ImageView historyBackIv;
    /**
     * 撤销textView
     **/
    private TextView historyDeleteCancel;
    /**
     * 编辑或者完成
     **/
    private TextView completeOrEdit;
    /**
     * 一屏幕展示完历史记录，余下的空间，用来注册点击事件
     **/
    private LinearLayout historyLeftLayout;

    private VideoHistoryBean rec_bean;

    private final int DIVIDE_WIDTH = 5;
    /**
     * 删除列表
     **/
    private List<VideoHistoryBean> deleteList = new ArrayList<VideoHistoryBean>();
    /**
     * 正在播放的map
     **/
    private Map<Integer, VideoHistoryBean> playMap = new HashMap<Integer, VideoHistoryBean>();

    private LinearLayout video_history_layout;

    private LinearLayout history_foot_layout;
    private LifeCycle lifeCycleListener;

    public LifeCycle getLifeCycleListener() {
        return lifeCycleListener;
    }

    public void setLifeCycleListener(LifeCycle lifeCycleListener) {
        this.lifeCycleListener = lifeCycleListener;
    }

    public interface LifeCycle {
        void onCreate();

        void onResume();

        void onHiddenChanged();

        void onPause();

        void onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_history_fragment;
    }

    @Override
    public void createView(LayoutInflater inflater, ViewGroup container, View parentView, Bundle savedInstanceState) {
        videoHistoryGv = (GridViewWithHeaderAndFooter) parentView.findViewById(R.id.video_history_gv);
        View footView = LayoutInflater.from(getActivity()).inflate(R.layout.history_7_day_foot, null);
        history_foot_layout = (LinearLayout) footView.findViewById(R.id.history_foot_layout);
        history_foot_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historyList != null && historyList.size() > 0) {
                    cancelPlay();
                }
            }
        });
        videoHistoryGv.addFooterView(footView);
        historyBackLayout = (RelativeLayout) parentView.findViewById(R.id.history_back_layout);
        historyBackLayout.setOnClickListener(this);
        historyBackIv = (ImageView) parentView.findViewById(R.id.history_back_iv);
        historyBackIv.setVisibility(View.VISIBLE);
        historyDeleteCancel = (TextView) parentView.findViewById(R.id.history_delete_cancel);
        historyDeleteCancel.setVisibility(View.GONE);

        completeOrEdit = (TextView) parentView.findViewById(R.id.history_complete_or_edit);
        completeOrEdit.setOnClickListener(this);
        historyLeftLayout = (LinearLayout) parentView.findViewById(R.id.history_left_layout);
        historyLeftLayout.setOnClickListener(this);

        video_history_layout = (LinearLayout) parentView.findViewById(R.id.video_history_layout);
        if (lifeCycleListener != null) {
            lifeCycleListener.onCreate();
        }
    }


    @Override
    public void init(Bundle savedInstanceState) {

        imOrFriend = getArguments().getString(FROM);

        //获取历史列表数据
        historyList = VideoUtils.showVideoList(VideoUtils.video7DayFileName(VideoUtils.getRecordVideoPath()), false);

        //录制
        rec_bean = new VideoHistoryBean();
        rec_bean.setVideoName("");
        rec_bean.setEditState(false);
        rec_bean.setRec(true);
        historyList.add(rec_bean);

        //由于该gridView距离左右都是10dp，设置的horizontalSpace和verticalSpace都为5dp
        int divide_width = VideoUtils.dp2px(getActivity(), DIVIDE_WIDTH);
        int margin_width = VideoUtils.dp2px(getActivity(), DIVIDE_WIDTH * 2);
        int item_width = (VideoUtils.getScreenWidth(getActivity()) - divide_width * 2 - margin_width * 2) / 3;

        LinearLayout.LayoutParams rLayoutParams = (LinearLayout.LayoutParams) history_foot_layout.getLayoutParams();
        rLayoutParams.height = item_width * 4 / 5;
        history_foot_layout.setLayoutParams(rLayoutParams);

        videoHistoryAdapter = new VideoHistoryAdapter(getActivity(), historyList, item_width, videoHistoryGv,
                deleteList, historyBackIv, historyDeleteCancel, completeOrEdit, playMap, imOrFriend, video_history_layout);

        videoHistoryGv.setAdapter(videoHistoryAdapter);
        videoHistoryAdapter.setListener(videoHistoryListener);
        videoHistoryAdapter.setMoreListener(new VideoHistoryAdapter.MoreListener() {
            @Override
            public void onMoreClickListener() {
                getFragmentManager().beginTransaction().hide(VideoHistoryFragment.this).commit();
            }
        });
        //滚动到最后一页面
        videoHistoryGv.setSelection(historyList.size() - 1);
    }

    private VideoHistoryAdapter.VideoHistoryListener videoHistoryListener;

    public void setVideoHistoryListener(VideoHistoryAdapter.VideoHistoryListener listener) {
        this.videoHistoryListener = listener;
        if (videoHistoryAdapter != null) {
            videoHistoryAdapter.setListener(videoHistoryListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history_back_layout:
                //返回或者撤销删除或者取消放大
                if (historyBackIv.getVisibility() == View.VISIBLE
                        && historyDeleteCancel.getVisibility() == View.GONE) {
                    if (!TextUtils.isEmpty(imOrFriend) && imOrFriend.equals(VideoHistoryFragment.IM)) {
                        getFragmentManager().beginTransaction().hide(VideoHistoryFragment.this).commit();
                    } else {
                        getActivity().finish();
                    }

                } else if (historyBackIv.getVisibility() == View.GONE
                        && historyDeleteCancel.getVisibility() == View.VISIBLE) {
                    if (historyDeleteCancel.getText().toString().trim()
                            .equals(getResources().getString(R.string.history_scale_cancel))) {
                        cancelPlay();
                    } else if (historyDeleteCancel.getText().toString().trim()
                            .equals(getResources().getString(R.string.history_delete_cancel))) {
                        //撤销删除
                        historyList.clear();
                        playMap.clear();
                        video_history_layout.setBackgroundColor(getResources().getColor(R.color.white));
                        videoHistoryAdapter.deleteList.clear();
                        historyList.addAll(VideoUtils.showVideoList(VideoUtils.video7DayFileName(VideoUtils.getRecordVideoPath()), true));
                        videoHistoryAdapter.notifyDataSetChanged();
                        if (historyDeleteCancel.getVisibility() == View.VISIBLE) {
                            historyDeleteCancel.setVisibility(View.GONE);
                        }
                        completeOrEdit.setText(R.string.history_complete);
                        if (completeOrEdit.getVisibility() == View.GONE) {
                            completeOrEdit.setVisibility(View.VISIBLE);
                        }
                        if (historyBackIv.getVisibility() == View.VISIBLE) {
                            historyBackIv.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case R.id.history_complete_or_edit:
                //完成或者编辑状态
                if (completeOrEdit.getText().toString().trim()
                        .equals(getResources().getString(R.string.history_edit))) {
                    // 变成编辑状态
                    historyList.remove(historyList.size() - 1);
                    for (int i = 0; i < historyList.size(); i++) {
                        VideoHistoryBean vb = historyList.get(i);
                        vb.setEditState(true);
                    }
                    completeOrEdit.setText(R.string.history_complete);
                    if (historyBackIv.getVisibility() == View.VISIBLE) {
                        historyBackIv.setVisibility(View.GONE);
                    }
                    videoHistoryAdapter.notifyDataSetChanged();
                } else if (completeOrEdit.getText().toString().trim()
                        .equals(getResources().getString(R.string.history_complete))) {
                    //编辑完成
                    if (videoHistoryAdapter.deleteList != null && videoHistoryAdapter.deleteList.size() > 0) {
                        for (int i = 0; i < videoHistoryAdapter.deleteList.size(); i++) {
                            //删除文件
                            String deleteFileName = videoHistoryAdapter.deleteList.get(i).getVideoName();
                            VideoUtils.deleteVideoAndThumbnail(VideoUtils.getRecordVideoPath() + deleteFileName);
                        }
                        videoHistoryAdapter.deleteList.clear();
                    }
                    for (int i = 0; i < historyList.size(); i++) {
                        VideoHistoryBean vb = historyList.get(i);
                        vb.setEditState(false);
                    }
                    playMap.clear();
                    video_history_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    if (historyDeleteCancel.getVisibility() == View.VISIBLE) {
                        historyDeleteCancel.setVisibility(View.GONE);
                    }
                    if (historyBackIv.getVisibility() == View.GONE) {
                        historyBackIv.setVisibility(View.VISIBLE);
                    }

                    historyList.add(rec_bean);
                    completeOrEdit.setText(R.string.history_edit);
                    videoHistoryAdapter.notifyDataSetChanged();
                }

                break;
            case R.id.history_left_layout:
                //如果一屏幕展示完历史记录，剩下的有空间的话
                if (historyList != null && historyList.size() > 0) {
                    cancelPlay();
                }
                break;
        }
    }

    /**
     * 取消放大
     */
    private void cancelPlay() {
        //取消放大
        if (historyList.get(0).isEditState()) {
            //如果之前是编辑状态，因为如果是编辑状态，所有的都是编辑状态，所以只需要获取第0个就行
            if (videoHistoryAdapter.deleteList != null && videoHistoryAdapter.deleteList.size() > 0) {
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

        for (int key : playMap.keySet()) {
            if (playMap.get(key) != null) {
                VideoUtils.updateSmallItemView(videoHistoryGv, getActivity(), key);
            }
        }
        playMap.clear();
        video_history_layout.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (lifeCycleListener != null) {
            lifeCycleListener.onHiddenChanged();
        }
        if (!hidden) {
            if (historyList != null) {
                historyList.clear();
                historyList.addAll(VideoUtils.showVideoList(VideoUtils.video7DayFileName(VideoUtils.getRecordVideoPath()), false));
                //录制
                rec_bean = new VideoHistoryBean();
                rec_bean.setVideoName("");
                rec_bean.setEditState(false);
                rec_bean.setRec(true);
                historyList.add(rec_bean);
            }
            videoHistoryAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (historyList != null) {
            historyList.clear();
            historyList.addAll(VideoUtils.showVideoList(VideoUtils.video7DayFileName(VideoUtils.getRecordVideoPath()), false));
            //录制
            rec_bean = new VideoHistoryBean();
            rec_bean.setVideoName("");
            rec_bean.setEditState(false);
            rec_bean.setRec(true);
            historyList.add(rec_bean);
        }
        videoHistoryAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private Menu mOptionsMenu;
    private MenuItem editItem;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_video_history, menu);
        mOptionsMenu = menu;
        editItem = mOptionsMenu.findItem(R.id.editItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                //完成或者编辑状态
                if (editItem.getTitle().equals(getResources().getString(R.string.history_edit))) {
                    // 变成编辑状态
                    historyList.remove(historyList.size() - 1);
                    for (int i = 0; i < historyList.size(); i++) {
                        VideoHistoryBean vb = historyList.get(i);
                        vb.setEditState(true);
                    }
                    editItem.setTitle(R.string.history_complete);
                    if (historyBackIv.getVisibility() == View.VISIBLE) {
                        historyBackIv.setVisibility(View.GONE);
                    }
                    videoHistoryAdapter.notifyDataSetChanged();
                } else if (editItem.getTitle().toString().trim()
                        .equals(getResources().getString(R.string.history_complete))) {
                    //编辑完成
                    if (videoHistoryAdapter.deleteList != null && videoHistoryAdapter.deleteList.size() > 0) {
                        for (int i = 0; i < videoHistoryAdapter.deleteList.size(); i++) {
                            //删除文件
                            String deleteFileName = videoHistoryAdapter.deleteList.get(i).getVideoName();
                            VideoUtils.deleteVideoAndThumbnail(VideoUtils.getRecordVideoPath() + deleteFileName);
                        }
                        videoHistoryAdapter.deleteList.clear();
                    }
                    for (int i = 0; i < historyList.size(); i++) {
                        VideoHistoryBean vb = historyList.get(i);
                        vb.setEditState(false);
                    }
                    playMap.clear();
                    video_history_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    if (historyDeleteCancel.getVisibility() == View.VISIBLE) {
                        historyDeleteCancel.setVisibility(View.GONE);
                    }
                    if (historyBackIv.getVisibility() == View.GONE) {
                        historyBackIv.setVisibility(View.VISIBLE);
                    }
                    historyList.add(rec_bean);
                    editItem.setTitle(R.string.history_edit);
                    videoHistoryAdapter.notifyDataSetChanged();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
