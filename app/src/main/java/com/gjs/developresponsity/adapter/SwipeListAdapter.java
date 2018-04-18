package com.gjs.developresponsity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.gjs.developresponsity.R;

import java.util.ArrayList;
import java.util.List;

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

    public SwipeListAdapter(Context context, List<String> dataObjects){
        super(context, R.layout.item_list,dataObjects);
        mLayoutInflater = LayoutInflater.from(context);
        mDataList = dataObjects;
        mBinderhelper = new ViewBinderHelper();
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

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.content = convertView.findViewById(R.id.list_content);
            holder.delete = convertView.findViewById(R.id.list_delete);
            holder.swipeRevealLayout = convertView.findViewById(R.id.list_swipe);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String item = getItem(i);
        if(item != null) {
            mBinderhelper.bind(holder.swipeRevealLayout, item);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(item);
                }
            });
            holder.content.setText(item);
        }

        return convertView;
    }

    private class ViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private TextView content;
        private TextView delete;
    }

}
