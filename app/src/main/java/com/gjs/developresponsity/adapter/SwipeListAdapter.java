package com.gjs.developresponsity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.gjs.developresponsity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/04/16
 *     desc    : List的adapter
 *     version : 1.0
 * </pre>
 */

public class SwipeListAdapter extends ArrayAdapter {

    private List<String> mDataList = new ArrayList<>();
    private final LayoutInflater mLayoutInflater;
    private final ViewBinderHelper mBinderhelper;
    private HashMap<String, Boolean> mSelectList;
    private boolean isSelectAll = true;

    private boolean isVisible = false;

    public SwipeListAdapter(Context context, List<String> dataObjects,HashMap<String,Boolean> selectList){
        super(context, R.layout.item_list,dataObjects);
        mLayoutInflater = LayoutInflater.from(context);
        mDataList = dataObjects;
        mBinderhelper = new ViewBinderHelper();
        this.mSelectList = selectList;
    }

    public void setSelectAll(boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
        selectAll();
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public String getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setCheckVisible(boolean visible){
        this.isVisible = visible;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.list_content);
            holder.delete = convertView.findViewById(R.id.list_delete);
            holder.checkBox = convertView.findViewById(R.id.list_select_item);
            holder.swipeRevealLayout = convertView.findViewById(R.id.list_swipe);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String item = getItem(i);
        if(item != null) {
            holder.content.setText(item);
            mBinderhelper.bind(holder.swipeRevealLayout, item);
            if(isVisible) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }

            holder.checkBox.setChecked(mSelectList.containsKey(item));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(item);
                    notifyDataSetChanged();
                    for (Map.Entry entry:mSelectList.entrySet()){
                        System.out.println("key : " + entry.getKey() + " value : " + entry.getValue());
                    }
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mSelectList.containsKey(item)) {
                        mSelectList.put(item, true);
                    } else {
                        mSelectList.remove(item);
                    }

//                    mSelectList.put(item,!mSelectList.containsKey(item));
                    notifyDataSetChanged();
                    for (Map.Entry entry:mSelectList.entrySet()){
                        System.out.println("key : " + entry.getKey() + " value : " + entry.getValue());
                    }
                }
            });
        }

        return convertView;
    }

    public HashMap<String,Boolean> selectAll(){
        if(isSelectAll){
            isSelectAll = false;
            for (String item:mDataList) {
                mSelectList.put(item,true);
            }
        } else {
            isSelectAll = true;
            mSelectList.clear();
        }
        notifyDataSetChanged();
        return mSelectList;
    }

    private class ViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private TextView content;
        private TextView delete;
        private CheckBox checkBox;
    }

}
