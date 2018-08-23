package com.gjs.developresponsity.utils.permission;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gjs.developresponsity.R;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/28
 *     desc    : 权限管理对话框
 *     version : 1.0
 * </pre>
 */

public class PermissionDialog {

    private final Context mContext;
    private final onConfirmListener mListener;
    private Dialog mDialog;
    private TextView mTv;
    private View mDialogContentView;

    public PermissionDialog(Context context,onConfirmListener listener){
        this.mContext = context;
        this.mListener = listener;
        init();
    }

    private void init(){
        mDialog = new Dialog(mContext, R.style.custom_dialog2);
        mDialogContentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_permission,null);
        mTv = mDialogContentView.findViewById(R.id.tv_dialog_permission_content);
        Button btnConfirm = mDialogContentView.findViewById(R.id.btn_dialog_permission_comfrim);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(mListener != null) {
                    mListener.confirm();
                }
            }
        });
        mDialog.setContentView(mDialogContentView);
    }

    public void setmTvContent(String content){
        this.mTv.setText(content);
    }

    private void dismiss(){
        mDialog.dismiss();
    }

    public void show(){
        mDialog.show();
    }

    public interface onConfirmListener{
        void confirm();
    }

}
