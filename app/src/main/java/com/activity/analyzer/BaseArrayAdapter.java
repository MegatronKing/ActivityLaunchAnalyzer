package com.activity.analyzer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The adapter is based on {@link BaseAdapter} for array data.
 *
 * @author Megatron King
 * @since 2015-6-30 上午11:07:34
 * @param <T> Data type
 */
public abstract class BaseArrayAdapter<T> extends BaseAdapter {

	private List<T> mData = new ArrayList<T>();
	protected Context mContext;
	protected LayoutInflater mInflater;

	public BaseArrayAdapter(Context context) {
		this(context, null);
	}
	
	public BaseArrayAdapter(Context context, List<T> data) {
		if(data == null){
			data = new ArrayList<T>();
		}
		this.mData.addAll(data);
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData == null ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void appendData(List<T> data){
		mData.addAll(data);
	}
	
	public void appendData(T[] data){
		mData.addAll(Arrays.asList(data));
	}
	
	public void appendData(T data){
		mData.add(data);
	}

	public void insertData(T data, int location){
		mData.add(location, data);
	}
	
	public void clearData(){
		mData.clear();
	}
	
	public void replaceData(List<T> data){
		clearData();
		mData.addAll(data);
	}
	
	public void replaceData(T[] data){
		clearData();
		mData.addAll(Arrays.asList(data));
	}
	
	public void removeData(T data){
		mData.remove(data);
	}
	
	public void removeData(int position){
		mData.remove(position);
	}

	public List<T> getData(){
		return mData;
	}
}
