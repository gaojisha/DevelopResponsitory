package com.gjs.developresponsity.video.tools;

import android.view.View;

public abstract class NoDoubleClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 800;

    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long currentClickTime = System.currentTimeMillis();
        if(currentClickTime - lastClickTime > MIN_CLICK_DELAY_TIME){
            lastClickTime = currentClickTime;
            onNoDoubleClick(view);
        }
    }

    public abstract void onNoDoubleClick(View view);
}
