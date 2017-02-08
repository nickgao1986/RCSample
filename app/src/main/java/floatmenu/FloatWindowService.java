package floatmenu;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FloatWindowService extends Service {
    private static final String TAG = "[RC]FloatWindowService";

	private Handler handler = new Handler();

	private Timer timer;

    private UnreadIndicatorOnTopMenuButtonReceiver mUnreadIndicatorReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mUnreadIndicatorReceiver = new UnreadIndicatorOnTopMenuButtonReceiver();
        IntentFilter intentFilter = new IntentFilter(RCMConstants.ACTION_UNREAD_INDICATOR_CHANGED);
        registerReceiver(mUnreadIndicatorReceiver, intentFilter);
    }

    @Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		timer = null;
        unregisterUnreadIndicatorReceiver();
	}

    private void unregisterUnreadIndicatorReceiver() {
        if (mUnreadIndicatorReceiver != null) {
            try {
                unregisterReceiver(mUnreadIndicatorReceiver);
            } catch (Throwable th) {
                MktLog.e(TAG, "onDestroy(): unregisterReceiver(mSwitchTabReceiver) : exception :" + th.toString(), th);
            }

            mUnreadIndicatorReceiver = null;
        }
    }

	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			if (isHome() && !MyWindowManager.isWindowShowing()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (Build.VERSION.SDK_INT >= 23) {
							if (Settings.canDrawOverlays(FloatWindowService.this)) {
								showFloatView();
							} else {
								Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
								startActivity(intent);
							}
						} else {
							showFloatView();
						}

					}
				});
			}
			else if (!isHome() && MyWindowManager.isWindowShowing()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						MyWindowManager.removeSmallWindow(getApplicationContext());
						MyWindowManager.removeBigWindow(getApplicationContext());
					}
				});
			}
		}

	}

	private void showFloatView() {
		MyWindowManager.createSmallWindow(getApplicationContext());
	}

	private boolean isHome() {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return getHomes().contains(rti.get(0).topActivity.getPackageName());
	}

	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

    private class UnreadIndicatorOnTopMenuButtonReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyWindowManager.updateMessageCount();
            MyWindowManager.updateSmallMessageCount(context);
        }
    }
}
