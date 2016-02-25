package com.activity.analyzer.library;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The custom toast with color level.
 *
 * @author Megatron King
 * @since 2016-2-19 下午4:07:26
 */
public class LevelToast {

	public static final int LEVEL_NORMAL = 0;
	public static final int LEVEL_SLIGHT = 1;
	public static final int LEVEL_WARNING = 2;
	public static final int LEVEL_ALERT = 3;
	public static final int LEVEL_FATAL = 4;
	
	public static void showToast(Context context, String text, int level){
		LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView v = (TextView) inflate.inflate(R.layout.debug_level_toast, null);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(getColorByLevel(level));
        v.setText(text);
        Toast toast = new Toast(context);
    	toast.setDuration(Toast.LENGTH_SHORT);
    	toast.setView(v);
    	toast.show();
	}
	
	public static int getLevel(long n){
		if(n <= 125){
			return LEVEL_NORMAL;
		}
		if(n <= 250){
			return LEVEL_SLIGHT;
		}
		if(n <= 500){
			return LEVEL_WARNING;
		}
		if(n <= 1000){
			return LEVEL_ALERT;
		}
		return LEVEL_FATAL;
	}

	public static int getColorByLevel(int level){
		int color = 0;
		switch (level) {
		case LEVEL_NORMAL:
			color = 0xFF00FF00;
			break;
		case LEVEL_SLIGHT:
			color = 0xFFBBFF00;
			break;
		case LEVEL_WARNING:
			color = 0xFFFFFF00;
			break;
		case LEVEL_ALERT:
			color = 0xFFFF6600;
			break;
		case LEVEL_FATAL:
			color = 0xFFFF0000;
			break;
		default:
			break;
		}
		return color;
	}
}
