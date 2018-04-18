package com.gjs.developresponsity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.gjs.developresponsity.R;
import com.gjs.developresponsity.model.YourDataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/04/16
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */

public class SwipeGridAdapter extends ArrayAdapter {

    private Context mContext;
    private ViewBinderHelper mViewBindHelper;

    public SwipeGridAdapter(Context context,List<YourDataObject> dataObjects){
        super(context,R.layout.item_grid, dataObjects);
        mContext = context;
        mViewBindHelper = new ViewBinderHelper();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final YourDataObject dataObject = (YourDataObject) getItem(i);
        GridViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid, parent,false);
            viewHolder = new GridViewHolder();
            viewHolder.swipeRevealLayout = convertView.findViewById(R.id.grid_swipe);
            viewHolder.content = convertView.findViewById(R.id.grid_content);
            viewHolder.delete = convertView.findViewById(R.id.grid_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) convertView.getTag();
        }
        if(dataObject != null){
            mViewBindHelper.bind(viewHolder.swipeRevealLayout, dataObject.getId());
        }
        viewHolder.content.setText(dataObject.getText());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(dataObject);
            }
        });

        return convertView;
    }

    private class GridViewHolder{
        private SwipeRevealLayout swipeRevealLayout;
        private TextView content;
        private TextView delete;
    }

}
