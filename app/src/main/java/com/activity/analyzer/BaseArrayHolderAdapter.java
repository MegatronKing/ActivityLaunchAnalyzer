package com.activity.analyzer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Integrate {@link ViewHolder}
 *
 * @author Megatron King
 * @since 2015/12/21 17:36
 */
public abstract class BaseArrayHolderAdapter<T> extends BaseArrayAdapter<T>{

    protected abstract int getViewResId();
    protected abstract void bindDataToView(View convertView, T t, int position);

    public BaseArrayHolderAdapter(Context context) {
        super(context);
    }

    public BaseArrayHolderAdapter(Context context, List<T> data) {
        super(context, data);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(getViewResId(), parent, false);
        }
        bindDataToView(convertView, getItem(position), position);
        return convertView;
    }

    protected <O extends View> O findView(View convertView, int id){
        return ViewHolder.get(convertView, id);
    }
}
