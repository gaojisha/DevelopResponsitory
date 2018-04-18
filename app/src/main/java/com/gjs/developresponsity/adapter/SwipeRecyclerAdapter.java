package com.gjs.developresponsity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 *     time    : 2018/04/12
 *     desc    : 添加了swipeRevalLayout的RecycleView的adapter
 *     version : 1.0
 * </pre>
 */

public class SwipeRecyclerAdapter extends RecyclerView.Adapter {

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private List<YourDataObject> mDataSet = new ArrayList<>();
    LayoutInflater mInflater;

    public SwipeRecyclerAdapter(Context context, List<YourDataObject> datalist){
        mInflater = LayoutInflater.from(context);
        mDataSet = datalist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.item_recycler, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // get your data object first.
        YourDataObject dataObject = mDataSet.get(position);

        // Save/restore the open/close state.
        // You need to provide a String id which uniquely defines the data object.
        if(holder != null) {
            viewBinderHelper.bind(((ViewHolder) holder).swipeRevealLayout, dataObject.getId());
            ((ViewHolder) holder).bind(dataObject);
        }
        // do your regular binding stuff here
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinderHelper.restoreStates(inState);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private SwipeRevealLayout swipeRevealLayout;
        private TextView content;
        private TextView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.recycler_swipe);
            content = itemView.findViewById(R.id.recycler_content);
            delete = itemView.findViewById(R.id.recycler_delete);
        }

        public void bind(YourDataObject dataObject){
            content.setText(dataObject.getText());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDataSet.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

    }

}
