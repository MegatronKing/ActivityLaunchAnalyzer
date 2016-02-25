package com.activity.analyzer.library;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Some methods about reflecting ViewRootImpl.
 *
 * @author Megatron King
 * @since 2016-2-24 下午5:53:29
 */
public class ViewRootImplUtil {
	
	public static ViewTreeObserver getViewRootImplTreeObserver(Activity activity)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Object viewRootImpl = getViewRootImpl(activity);
		if (viewRootImpl != null) {
			Object attachInfo = reflectFieldValue(viewRootImpl, "mAttachInfo");
			if(attachInfo != null){
				return (ViewTreeObserver) reflectFieldValue(attachInfo, "mTreeObserver");
			}
		}
		return null;
	}
	
	public static Object getViewRootImpl(Activity activity)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		View hostView;
		if(Build.VERSION.SDK_INT > 16){
			Object windowManagerGlobal = reflectFieldValue(activity.getWindowManager(), "mGlobal");
			Object roots = reflectFieldValue(windowManagerGlobal, "mRoots");
			if(roots == null){
				return null;
			}
			if(roots instanceof Object[]){
				Object[] rootArrays = (Object[])roots;
				for (int i = rootArrays.length - 1; i >= 0; i--) {
					hostView = (View) reflectFieldValue(rootArrays[i], "mView");
					if(hostView == activity.getWindow().getDecorView()){
						return rootArrays[i];
					}
				}
			}
			if(roots instanceof ArrayList){
				ArrayList<?> rootArrays = (ArrayList<?>)roots;
				for (int i = rootArrays.size() - 1; i >= 0; i--) {
					hostView = (View) reflectFieldValue(rootArrays.get(i), "mView");
					if(hostView == activity.getWindow().getDecorView()){
						return rootArrays.get(i);
					}
				}
			}
		}else{
			Object roots = reflectFieldValue(activity.getWindowManager(), "mRoots");
			if(roots == null){
				return null;
			}
			Object[] rootArrays = (Object[])roots;
			for (int i = rootArrays.length - 1; i >= 0; i--) {
				hostView = (View) reflectFieldValue(rootArrays[i], "mView");
				if(hostView == activity.getWindow().getDecorView()){
					return rootArrays[i];
				}
			}
		}
		return null;
	}
	
	private static Object reflectFieldValue(Object obj, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		if(field != null){
			if(!field.isAccessible()){
				field.setAccessible(true);
			}
			return field.get(obj);
		}
		return null;
	}

}
