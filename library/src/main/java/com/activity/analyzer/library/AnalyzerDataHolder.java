package com.activity.analyzer.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析数据暂存
 *
 * @author Megatron King
 * @since 2016/2/22 15:03
 */
public class AnalyzerDataHolder {

    private static Map<String, List<ActivityLaunchData>> sDataHolder = new HashMap<String, List<ActivityLaunchData>>();

    public static void add(ActivityLaunchData data){
        String componentName = data.getComponentName();
        List<ActivityLaunchData> launchDataList = get(componentName);
        if(launchDataList == null){
            launchDataList = new ArrayList<ActivityLaunchData>();
            sDataHolder.put(componentName, launchDataList);
        }
        launchDataList.add(data);
    }

    public static Map<String, List<ActivityLaunchData>> get(){
        return sDataHolder;
    }

    public static List<ActivityLaunchData> get(String componentName){
        return sDataHolder.get(componentName);
    }
}
