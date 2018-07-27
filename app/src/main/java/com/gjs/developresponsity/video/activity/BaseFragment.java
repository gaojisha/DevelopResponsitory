package com.gjs.developresponsity.video.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjs.developresponsity.utils.Constants;
import com.gjs.developresponsity.utils.base.LogUtils;
import com.gjs.developresponsity.video.tools.broadcast.DataBroadcast;

public abstract class BaseFragment extends Fragment implements DataBroadcast.DataBroadcasterListener {

	public boolean mHasNavBar = true; //不需要title的fragment
	public boolean mIsneedCleanNavBar = true;
	protected static final int MSG_SHOW_TOAST = 1001;

	private BroadcastReceiver receiver;
	protected View parentView;
	protected ProgressDialog mProgressDialog = null;
//	protected NavBarLayout navBarLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 设置可以弹出菜单
		setHasOptionsMenu(true);
		LogUtils.d("BaseFragment", "BaseFragment>>>>onCreateView");
		if (registerReceiver())
		{
			receiver = getDataBroadcase().getReceiver(this);
			IntentFilter filter = new IntentFilter();
			setBroadcaseFilter(filter);
			getDataBroadcase().registerReceiver(receiver, filter);
		}

		parentView = inflater.inflate(getLayoutId(), container, false);
		createView(inflater, container, parentView, savedInstanceState);

//		if (mHasNavBar) {
//			navBarLayout = ((BaseActivity) getActivity()).navBarLayout;
//			initTitle(navBarLayout);
//		}
		
		init(savedInstanceState);
		return parentView;
	}


	@Override
	public void onResume() {
		LogUtils.d("BaseFragment", "BaseFragment>>>>onResume");
		super.onResume();

		// long endTime = System.currentTimeMillis();
		// FinLog.e("init", "---------------- allDelayTime " + (endTime - ((MainTabsActivity)getActivity()).startTime) + " " + this);
	}

	/** step 1 */
	public abstract int getLayoutId();

	/** step 2 */
	public abstract void createView(LayoutInflater inflater, ViewGroup container, View parentView, Bundle savedInstanceState);

	/** step 3 */
//	public abstract void initTitle(NavBarLayout navBarLayout);

	/** step 4 */
	public abstract void init(Bundle savedInstanceState);

	public void startNewFragment(Class<? extends Fragment> cls, Bundle bundle, int enterAnim, int exitAnim) {

		Fragment fragment = Fragment.instantiate(getActivity(), cls.getName(), bundle);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		if (enterAnim != 0 && exitAnim != 0) {
			// transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}
		fragment.setTargetFragment(this, 0);
		transaction.add(android.R.id.content, fragment, fragment.getTag());
		transaction.addToBackStack("popup");
		transaction.commitAllowingStateLoss();

	}

	public void repalceNewFragment(Class<? extends Fragment> cls, Bundle bundle, int enterAnim, int exitAnim) {

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		Fragment fragment = Fragment.instantiate(getActivity(), cls.getName(), bundle);

		if (enterAnim != 0 && exitAnim != 0) {
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}

		transaction.replace(getId(), fragment);
		transaction.commitAllowingStateLoss();
	}

	public void repalceNewFragment(Fragment fragment, int layoutId, int enterAnim, int exitAnim) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		
		if (enterAnim != 0 && exitAnim != 0) {
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}

		transaction.replace(layoutId, fragment);
		transaction.commitAllowingStateLoss();
	}

	public void repalceNewFragment(Fragment fragment, int layoutId, String tag) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(layoutId, fragment, tag);
		transaction.commitAllowingStateLoss();
	}

	public void addNewFragment(Class<? extends Fragment> cls, int id, Bundle bundle, int enterAnim, int exitAnim) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment fragment = Fragment.instantiate(getActivity(), cls.getName(), bundle);
		if(transaction==null)
			return;
		if (enterAnim != 0 && exitAnim != 0) {
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}
		transaction.add(id, fragment);
		transaction.commitAllowingStateLoss();
	}

	public void addNewFragment(Fragment fragment, int id, String tag) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		if (tag == null) {
			transaction.add(id, fragment);
		}
		else {
			transaction.add(id, fragment, tag);
		}
		transaction.commitAllowingStateLoss();
	}

	public void addNewFragment(Fragment fragment, int id, int enterAnim, int exitAnim) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		if (enterAnim != 0 && exitAnim != 0) {
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}
		transaction.add(id, fragment);
		transaction.commitAllowingStateLoss();
	}

	public void startNewFragmentForResult(Class<? extends Fragment> cls, Bundle bundle, int requestCode, int enterAnim, int exitAnim) {
		Fragment fragment = Fragment.instantiate(getActivity(), cls.getName(), bundle);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if(transaction==null)
			return;
		if (enterAnim != 0 && exitAnim != 0) {
			transaction.setCustomAnimations(enterAnim, exitAnim);
		}
		fragment.setTargetFragment(this, requestCode);
		transaction.add(android.R.id.content, fragment, fragment.getTag());
		transaction.addToBackStack("popup");
		transaction.commitAllowingStateLoss();
	}

	public void removeFragment(Fragment fragment) {
		if(fragment != null) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.remove(fragment);
			transaction.commitAllowingStateLoss();
		}
	}

	protected Fragment findFaragment(int layoutId) {
		FragmentManager fm = getFragmentManager();
		return fm.findFragmentById(layoutId);
	}

	protected Fragment findFaragment(String tag) {
		FragmentManager fm = getFragmentManager();
		return fm.findFragmentByTag(tag);
	}

	public void hideFragment(Fragment fragment) {
		if(fragment != null) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.hide(fragment);
			transaction.commitAllowingStateLoss();
		}
	}

	public void showFragment(Fragment fragment) {
		if(fragment != null) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.show(fragment);
			transaction.commitAllowingStateLoss();
		}
	}

	public void onFregmentResult(Object object, int requestCode) {

	}

	public void setFregmentResult(Object object) {

		if (getTargetFragment() != null && getTargetFragment() instanceof BaseFragment) {
			BaseFragment baseFragment = (BaseFragment) getTargetFragment();
			int requestCode = getTargetRequestCode();
			baseFragment.onFregmentResult(object, requestCode);
		}
	}

	protected void close() {
		getFragmentManager().popBackStackImmediate();
	}

	private DataBroadcast getDataBroadcase() {
		return Constants.getInstance().getBroadcast();
	}

	protected abstract void setBroadcaseFilter(IntentFilter filter);

	protected abstract boolean registerReceiver();

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		if (registerReceiver()) {
			getDataBroadcase().unregisterReceiver(receiver);
		}
		
//		if (mIsneedCleanNavBar && navBarLayout != null) {
//			navBarLayout.resetLayout();
//			navBarLayout.removeAllViews();
//			navBarLayout = null;
//		}
	}
	
//	protected void showProgressDialog(int titleResId, int contentResId, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
//		String title = titleResId == 0 ? "" : getString(titleResId);
//		String content = contentResId == 0 ? getString(R.string.general_pleasewait) : getString(contentResId);
//		mProgressDialog = DialogFactory.createIndeterminateProgressDialog(getActivity(), title, content, indeterminate, cancelable, cancelListener);
//		mProgressDialog.show();
//	}

	protected void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	
	@Override
	public void onDestroy() {
		if (registerReceiver()) {
			getDataBroadcase().unregisterReceiver(receiver);
		}
		super.onDestroy();
//		System.gc();
	}
}
