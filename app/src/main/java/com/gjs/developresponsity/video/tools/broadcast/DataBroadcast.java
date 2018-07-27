package com.gjs.developresponsity.video.tools.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.gjs.developresponsity.video.tools.log.Trace;

public class DataBroadcast {

	private LocalBroadcastManager manager = null;

	public static final int TYPE_OPERATION_ADD = 0x100000;

	public static final int TYPE_OPERATION_UPDATE = 0x100001;

	public static final int TYPE_OPERATION_DELETE = 0x100002;

	public static final int TYPE_OPERATION_SUCCEED = 0x100003;

	public static final int TYPE_OPERATION_FAILED = 0x100004;

	public static final int TYPE_OPERATION_DEFAULT = 0x100005;

	public interface DataBroadcasterListener {
		void onReceive(String action, int type, Bundle bundle);
	}

	public DataBroadcast(Context context) {
		manager = LocalBroadcastManager.getInstance(context);

	}

	public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		if (manager != null) {
			manager.registerReceiver(receiver, filter);
		}
	}

	public BroadcastReceiver getReceiver(final DataBroadcasterListener listener) {

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("DataBroadcast", "DataBroadcast intent" + intent);
				if (listener != null) {
					String action = intent.getStringExtra("action");
					int type = intent.getIntExtra("type", TYPE_OPERATION_DEFAULT);
					listener.onReceive(action, type, intent.getExtras());
				}
			}
		};
		Log.d("DataBroadcast", "DataBroadcast receiver" + receiver);
		return receiver;
	}

	public void sendBroadcast(String action, int type, Bundle bundle) {
		Trace.T(action + " " + type + " " + (bundle == null ? "null" : bundle.toString()));
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		intent.putExtra("action", action);
		intent.putExtra("type", type);

		manager.sendBroadcast(intent);
	}

	public void sendBroadcast(String action, int type) {
		sendBroadcast(action, type, null);
	}

	// public void sendBroadcastSync(String action, int type, Bundle bundle) {
	// Intent intent = new Intent();
	// intent.putExtra("action", action);
	// intent.putExtra("type", type);
	// if (bundle != null) {
	// intent.putExtras(bundle);
	// }
	// manager.sendBroadcastSync(intent);
	// }
	//
	// public void sendBroadcastSync(String action, int type) {
	// sendBroadcastSync(action, type, null);
	// }

	public void unregisterReceiver(BroadcastReceiver receiver) {
		manager.unregisterReceiver(receiver);
	}
}
