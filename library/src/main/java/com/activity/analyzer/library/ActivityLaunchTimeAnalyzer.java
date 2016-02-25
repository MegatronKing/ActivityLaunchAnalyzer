package com.activity.analyzer.library;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A tool for analyzing the launch time of Activity.<br><br>
 *
 * Application called: 
 * <code>
 *    ActivityLaunchTimeAnalyzer.install(this);
 * </code>
 *
 * @author Megatron King
 * @since 2016-2-18 下午5:43:12
 */
public class ActivityLaunchTimeAnalyzer implements ActivityLifecycleCallbacks {

	private static final int DELAY_TIME = 1000;

	private Application mApplication;
	private Set<ActivityCreateData> mActivityCaches = new HashSet<ActivityCreateData>();
	private Handler mHandler = new Handler();

	private ActivityLaunchTimeAnalyzer(Application application){
		this.mApplication = application;
	}

	public static void install(Application application){
		ActivityLaunchTimeAnalyzer analyzer = new ActivityLaunchTimeAnalyzer(application);
		analyzer.start();
	}

	private void start(){
		grantPermission();
		if(checkPermission()){
			mApplication.registerActivityLifecycleCallbacks(this);
		}
	}

	private void grantPermission(){
		if(Build.VERSION.SDK_INT >= 16 && !checkPermission()){
			String packageName = mApplication.getPackageName();
			try {
				// format the commandline parameter
				Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", String.format("pm grant %s android.permission.READ_LOGS", packageName)});
				int res = p.waitFor();
				if (res != 0){
					Toast.makeText(mApplication, "Please run the follow in cmd:\nadb shell pm grant " + packageName + " android.permission.READ_LOGS", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mApplication, "Grant permission succeed, please restart the app-process", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private boolean checkPermission(){
		return mApplication.getPackageManager().checkPermission(android.Manifest.permission.READ_LOGS, mApplication.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}

	private void showToast(String text, int level){
		LevelToast.showToast(mApplication, text, level);
	}

	@Override
	public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
		final ActivityCreateData activityCreateData = new ActivityCreateData(activity, System.currentTimeMillis());
		mActivityCaches.add(activityCreateData);
		activity.getWindow().setCallback(
				new WindowCallbackWrapper(activity) {

					@Override
					public void onAttachedToWindow() {
						// The analyze code may cost some millis.
						long codeStartTime = System.currentTimeMillis();
						analyzeCreateTime(activity, activityCreateData);
						activityCreateData.codeTime = System.currentTimeMillis() - codeStartTime;
						super.onAttachedToWindow();
					}
				});
	}

	@Override
	public void onActivityStarted(final Activity activity) {
	}

	@Override
	public void onActivityResumed(Activity activity) {
		for (final ActivityCreateData activityCreateData : mActivityCaches) {
			if(activity == activityCreateData.activity){
				// The resume called usually after the event log write,
				// So we delay 1000ms, the may read the log regularly.
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						analyzeLaunchTime(activityCreateData);
						mActivityCaches.remove(activityCreateData);
					}
				}, DELAY_TIME);
			}
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
	}

	@Override
	public void onActivityStopped(Activity activity) {
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
	}

	private void analyzeCreateTime(Activity activity, final ActivityCreateData activityCreateData){
		try {
			final ViewTreeObserver observer = ViewRootImplUtil.getViewRootImplTreeObserver(activity);
			final long now = System.currentTimeMillis();
			if(observer != null){
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						observer.removeOnGlobalLayoutListener(this);
						activityCreateData.layoutTime = System.currentTimeMillis() - now;
					}
				});

                // API 16 method
				observer.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {

					@Override
					public void onDraw() {
						observer.removeOnDrawListener(this);
						activityCreateData.drawTime = System.currentTimeMillis() - now - activityCreateData.layoutTime;
					}
				});
			}
		} catch (IllegalArgumentException e) {
			// Ignore
		} catch (IllegalAccessException e) {
			// Ignore
		} catch (NoSuchFieldException e) {
			// Ignore
		}
	}

	private void analyzeLaunchTime(ActivityCreateData activityCreateData){
		List<Object> objects = EventLogFetcher.fetchDataAfter(EventLogFetcher.AM_ACTIVITY_LAUNCH_TIME, activityCreateData.createTime);
		if(objects == null || objects.isEmpty()){
			return;
		}
		ActivityLaunchData activityLaunchData = null;
		for (Object object : objects) {
			Object[] data = (Object[]) object;
			if(data == null || data.length != 5){
				continue;
			}
			ActivityLaunchData activityLaunchDataTemp = ActivityLaunchData.convert(data);
			if(activityCreateData.activity.getClass().getName().equals(activityLaunchDataTemp.getComponentName())){
				activityLaunchData = activityLaunchDataTemp;
			}
		}
		if(activityLaunchData != null){
			activityLaunchData.thisTime = activityLaunchData.thisTime - activityCreateData.codeTime;
			activityLaunchData.level = LevelToast.getLevel(activityLaunchData.thisTime);
			activityLaunchData.layoutTime = activityCreateData.layoutTime;
			activityLaunchData.drawTime = activityCreateData.drawTime;
			showToast(activityLaunchData.getPageShortName() + " launch time " + activityLaunchData.thisTime + "ms", activityLaunchData.level);
			AnalyzerDataHolder.add(activityLaunchData);
		}
	}

	private class ActivityCreateData{

		private Activity activity;
		private long createTime;
		private long layoutTime;
		private long drawTime;
		private long codeTime;

		public ActivityCreateData(Activity activity, long time) {
			this.activity = activity;
			this.createTime = time;
		}

	}

	private class WindowCallbackWrapper implements Window.Callback{

		private Window.Callback mCallback;

		public WindowCallbackWrapper(Window.Callback callback) {
			this.mCallback = callback;
		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			return mCallback != null && mCallback.dispatchKeyEvent(event);
		}

		@Override
		public boolean dispatchKeyShortcutEvent(KeyEvent event) {
			return mCallback != null && mCallback.dispatchKeyShortcutEvent(event);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			return mCallback != null && mCallback.dispatchTouchEvent(event);
		}

		@Override
		public boolean dispatchTrackballEvent(MotionEvent event) {
			return mCallback != null && mCallback.dispatchTrackballEvent(event);
		}

		@Override
		public boolean dispatchGenericMotionEvent(MotionEvent event) {
			return mCallback != null && mCallback.dispatchGenericMotionEvent(event);
		}

		@Override
		public boolean dispatchPopulateAccessibilityEvent(
				AccessibilityEvent event) {
			return mCallback != null && mCallback.dispatchPopulateAccessibilityEvent(event);
		}

		@Override
		public View onCreatePanelView(int featureId) {
			if(mCallback != null){
				return mCallback.onCreatePanelView(featureId);
			}
			return null;
		}

		@Override
		public boolean onCreatePanelMenu(int featureId, Menu menu) {
			return mCallback != null && mCallback.onCreatePanelMenu(featureId, menu);
		}

		@Override
		public boolean onPreparePanel(int featureId, View view, Menu menu) {
			return mCallback != null && mCallback.onPreparePanel(featureId, view, menu);
		}

		@Override
		public boolean onMenuOpened(int featureId, Menu menu) {
			return mCallback != null && mCallback.onCreatePanelMenu(featureId, menu);
		}

		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			return mCallback != null && mCallback.onMenuItemSelected(featureId, item);
		}

		@Override
		public void onWindowAttributesChanged(LayoutParams attrs) {
			if(mCallback != null){
				mCallback.onWindowAttributesChanged(attrs);
			}
		}

		@Override
		public void onContentChanged() {
			if(mCallback != null){
				mCallback.onContentChanged();
			}
		}

		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			if(mCallback != null){
				mCallback.onWindowFocusChanged(hasFocus);
			}
		}

		@Override
		public void onAttachedToWindow() {
			if(mCallback != null){
				mCallback.onAttachedToWindow();
			}
		}

		@Override
		public void onDetachedFromWindow() {
			if(mCallback != null){
				mCallback.onDetachedFromWindow();
			}
		}

		@Override
		public void onPanelClosed(int featureId, Menu menu) {
			if(mCallback != null){
				mCallback.onPanelClosed(featureId, menu);
			}
		}

		@Override
		public boolean onSearchRequested() {
			return mCallback != null && mCallback.onSearchRequested();
		}

		@Override
		public ActionMode onWindowStartingActionMode(Callback callback) {
			if(mCallback != null){
				return mCallback.onWindowStartingActionMode(callback);
			}
			return null;
		}

		@Override
		public void onActionModeStarted(ActionMode mode) {
			if(mCallback != null){
				mCallback.onActionModeStarted(mode);
			}
		}

		@Override
		public void onActionModeFinished(ActionMode mode) {
			if(mCallback != null){
				mCallback.onActionModeFinished(mode);
			}
		}
	}
}
