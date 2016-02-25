package com.activity.analyzer.library;

import android.util.EventLog;
import android.util.EventLog.Event;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 抓取EventLog的工具类
 *
 * @author Megatron King
 * @since 2016-2-18 下午5:43:56
 */
public class EventLogFetcher {

	private static final String TAG = EventLogFetcher.class.getSimpleName();
	
	private static final int SECONDS_OFFSET = 12;

	public static final String AM_ACTIVITY_FULLY_DRAWN_TIME = "am_activity_fully_drawn_time";
	public static final String AM_ACTIVITY_LAUNCH_TIME = "am_activity_launch_time";
	
	public static List<Event> fetch(String tagName){
		List<Event> events = new ArrayList<Event>();
		try {
			EventLog.readEvents(new int[]{EventLog.getTagCode(tagName)}, events);
		} catch (IOException e) {
			Log.wtf(TAG, e);
		}
		return events;
	}
	
	public static List<Object> fetchData(String tagName){
		List<Event> events = fetch(tagName);
		List<Object> objects = new ArrayList<Object>();
		for (Event event : events) {
			objects.add(event.getData());
		}
		return objects;
	}
	
	public static Event fetchLatest(String tagName){
		List<Event> events = fetch(tagName);
		return events.isEmpty() ? null : events.get(events.size() - 1);
	}
	
	public static Object fetchLatestData(String tagName){
		Event event = fetchLatest(tagName);
		return event == null ? null : event.getData();
	}
	
	public static List<Object> fetchDataAfter(String tagName, long startTime){
		List<Event> events = fetch(tagName);
		List<Object> objects = new ArrayList<Object>();
		for (Event event : events) {
			long time = getEventTimeInMills(event);
			if(time >= startTime){
				objects.add(event.getData());
			}
		}
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, String> fetchTags(){
		HashMap<Integer, String> tags = null;
		try {
			Method method = EventLog.class.getDeclaredMethod("readTagsFile");
			method.setAccessible(true);
			method.invoke(null);
			Field field = EventLog.class.getDeclaredField("sTagNames");
			field.setAccessible(true);
			tags = (HashMap<Integer, String>) field.get(null);
		} catch (Exception e) {
			Log.wtf(TAG, e);
		}
		return tags;
	}
	
	public static void logTags(){
		HashMap<Integer, String> tags = fetchTags();
		if(tags == null || tags.isEmpty()){
			Log.wtf(TAG, "The event tag is empty!");
		}else{
			for (int tagCode : tags.keySet()) {
				Log.i(TAG, "code: " + tagCode + " name: " + tags.get(tagCode));
			}
		}
	}
	
	private static long getEventTimeInMills(Event event){
		long time = 0;
		try {
			Field field = event.getClass().getDeclaredField("mBuffer");
			field.setAccessible(true);
			ByteBuffer buffer = (ByteBuffer) field.get(event);
			// The will some milliseconds lost, add 1000ms
			time = ((long)buffer.getInt(SECONDS_OFFSET)) * 1000L + 1000L;
		} catch (Exception e) {
			Log.wtf(TAG, e);
		}
		return time;
	}
}
