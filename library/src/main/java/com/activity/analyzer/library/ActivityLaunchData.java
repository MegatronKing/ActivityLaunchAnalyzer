package com.activity.analyzer.library;

import java.io.Serializable;

/**
 * Activity启动数据
 *
 * @author Megatron King
 * @since 2016-2-18 下午5:53:37
 */
public class ActivityLaunchData implements Serializable {

	/**
	 * The user id of the user.
	 */
	public int userId;

	/**
	 * The hashcode of the ActivityRecord, {@link System#identityHashCode(Object)}.
	 */
	public int identityHashCode;

	/**
	 * The componentName in AndroidManifest, like 'com.example.test/.TestActivity'.
	 */
	public String shortComponentName;

	/**
	 * The launch time.
	 */
	public long thisTime;

	/**
	 * The total time include the initial start time of ActivityStack,
	 * normal is equals to launch time.
	 */
	public long totalTime;

	/**
	 * The layout time of view hierarchy.
	 */
	public long layoutTime;

	/**
	 * The draw time of view hierarchy.
	 */
	public long drawTime;

	/**
	 * The level of launch delay.
	 */
	public int level;

	public String getComponentName(){
		if(shortComponentName == null){
			return null;
		}
		if(shortComponentName.contains("/")){
			String[] arrays = shortComponentName.split("/");
			String packageName = arrays[0];
			String className = arrays[1];
			if(className.startsWith(".")){
				className = packageName + className;
			}
			return className;
		}
		return shortComponentName;
	}

	public String getPageShortName(){
		if(shortComponentName == null){
			return null;
		}
		int dotIndex = shortComponentName.lastIndexOf(".");
		if(dotIndex < 0 || dotIndex == shortComponentName.length() - 1){
			return null;
		}
		return shortComponentName.substring(dotIndex + 1);
	}

	public static ActivityLaunchData convert(Object[] data){
		ActivityLaunchData activityLaunchData = new ActivityLaunchData();
		activityLaunchData.userId = (Integer)data[0];
		activityLaunchData.identityHashCode = (Integer) data[1];
		activityLaunchData.shortComponentName = (String) data[2];
		activityLaunchData.thisTime = (Long) data[3];
		activityLaunchData.totalTime = (Long) data[4];
		return activityLaunchData;
	}
}
