package com.gjs.developresponsity.video.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjs.developresponsity.R;
import com.gjs.developresponsity.utils.VideoUtils;

/**
 * 没有标题的弹出框
 */
public class NoTitleCornerDialog extends Dialog {

	private int screen_width = 0;

	/**弹出框内容**/
	private TextView no_title_corner_dialog_content;
	/**弹出框确定按钮**/
	private TextView no_title_corner_dialog_sure_tv;
	/**弹出框取消按钮**/
	private TextView no_title_corner_dialog_cancel_tv;
	/**按钮之间分割线**/
	private View no_title_corner_btn_line;

	private LinearLayout no_title_corner_dialog_cancel_layout;

	private Context context;
	public interface NoTitleCornerDialogCallBack {

		public void onNoTitleCornerDialogSure();

		public void onNoTitleCornerDialogCancel();
	}

	private NoTitleCornerDialogCallBack noTitleCornerDialogCallBack;

	public NoTitleCornerDialog(Context context, int theme) {
		super(context, theme);
		//按返回键可以取消
//		setCancelable(false);
		this.context = context;
		setCanceledOnTouchOutside(false);
	}

	public NoTitleCornerDialog(Context context) {
		this(context, R.style.feinno_dialog_style);
		this.context = context;
		//屏幕宽度
		screen_width = VideoUtils.getScreenWidth(context);

		addDialogView(R.layout.no_title_dialog_suer_cancel);

	}


	private void addDialogView(int layout_id){
		//必须加上这么一句加载布局的语句
		super.setContentView(layout_id);

		LinearLayout no_title_corner_dialog_layout = (LinearLayout)findViewById(R.id.no_title_corner_dialog_layout);
		//设置dialog的宽高
		LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams)no_title_corner_dialog_layout.getLayoutParams();
		lLayoutParams.width = screen_width * 3 / 4;
		lLayoutParams.height = lLayoutParams.width / 2;
		no_title_corner_dialog_layout.setLayoutParams(lLayoutParams);

		//确定
		LinearLayout no_title_corner_dialog_sure_layout = (LinearLayout)findViewById(R.id.no_title_corner_dialog_sure_layout);
		no_title_corner_dialog_sure_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if(noTitleCornerDialogCallBack != null){
					noTitleCornerDialogCallBack.onNoTitleCornerDialogSure();
				}
			}

		});

		no_title_corner_btn_line = findViewById(R.id.no_title_corner_btn_line);

		//取消
		no_title_corner_dialog_cancel_layout = (LinearLayout)findViewById(R.id.no_title_corner_dialog_cancel_layout);
		no_title_corner_dialog_cancel_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if(noTitleCornerDialogCallBack != null){
					noTitleCornerDialogCallBack.onNoTitleCornerDialogCancel();
				}
			}
		});

		no_title_corner_dialog_content = (TextView)findViewById(R.id.no_title_corner_dialog_content);

		no_title_corner_dialog_sure_tv = (TextView)findViewById(R.id.no_title_corner_dialog_sure_tv);

		no_title_corner_dialog_cancel_tv = (TextView)findViewById(R.id.no_title_corner_dialog_cancel_tv);
	}
	

	/**设置内容**/
	public void setDialogContent(String dialog_content){
		no_title_corner_dialog_content.setText(dialog_content);
	}
	
	/**设置内容**/
	public void setDialogContent(int dialog_content_id){
		no_title_corner_dialog_content.setText(dialog_content_id);
	}

	/**设置内容颜色**/
	public void setDialogContentColor(int dialogContentColor){
		no_title_corner_dialog_content.setTextColor(context.getResources().getColor(dialogContentColor));
	}
	
	/**设置确定按钮文字, 如果不设置默认为"确定"**/
	public void setDialogSureBtnName(String dialog_sure_btn_name){
		no_title_corner_dialog_sure_tv.setText(dialog_sure_btn_name);
	}
	
	/**设置确定按钮文字, 如果不设置默认为"确定"**/
	public void setDialogSureBtnName(int dialog_sure_btn_name_id){
		no_title_corner_dialog_sure_tv.setText(dialog_sure_btn_name_id);
	}

	/**设置确定按钮文字颜色**/
	public void setDialogSureBtnColor(int sureBtnColor){
		no_title_corner_dialog_sure_tv.setTextColor(context.getResources().getColor(sureBtnColor));
	}
	
	/**设置取消按钮文字, 如果不设置默认为"取消"**/
	public void setDialogCancelBtnName(String dialog_cancel_btn_name){
		no_title_corner_dialog_cancel_tv.setText(dialog_cancel_btn_name);
	}
	
	/**设置取消按钮文字, 如果不设置默认为"取消"**/
	public void setDialogCancelBtnName(int dialog_cancel_btn_name_id){
		no_title_corner_dialog_cancel_tv.setText(dialog_cancel_btn_name_id);
	}

	/**设置取消按钮文字颜色**/
	public void setDialogCancelBtnColor(int cancelBtnColor){
		no_title_corner_dialog_cancel_tv.setTextColor(context.getResources().getColor(cancelBtnColor));
	}
	
	//设置事件接口
	public void setNoTitleCornerDialogCallBack(NoTitleCornerDialogCallBack noTitleCornerDialogCallBack){
		this.noTitleCornerDialogCallBack = noTitleCornerDialogCallBack;
	}

	/**只显示一个按钮**/
	public void oneButtonDialog(){
		no_title_corner_dialog_cancel_layout.setVisibility(View.GONE);
		no_title_corner_btn_line.setVisibility(View.GONE);
	}

	/**显示两个按钮*,默认也是两个按钮*/
	public void twoButtonDialog(){
		no_title_corner_dialog_cancel_layout.setVisibility(View.VISIBLE);
		no_title_corner_btn_line.setVisibility(View.VISIBLE);
	}
}
