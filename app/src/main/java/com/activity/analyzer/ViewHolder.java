package com.activity.analyzer;

import android.util.SparseArray;
import android.view.View;

public final class ViewHolder {

	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View rootView, int viewId) {
		SparseArray<View> viewHolder = (SparseArray<View>) rootView.getTag();

		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			rootView.setTag(viewHolder);
		}

		View childView = viewHolder.get(viewId);
		if (childView == null) {
			childView = rootView.findViewById(viewId);
			viewHolder.put(viewId, childView);
		}

		return (T) childView;
	}
}
