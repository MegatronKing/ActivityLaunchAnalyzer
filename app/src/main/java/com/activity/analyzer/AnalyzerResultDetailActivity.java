package com.activity.analyzer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.analyzer.library.ActivityLaunchData;

import java.util.List;

public class AnalyzerResultDetailActivity extends Activity{

    public static final String EXTRAL_ACTIVITY_NAME = "name";
    public static final String EXTRAL_ACTIVITY_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra(EXTRAL_ACTIVITY_NAME));
        setContentView(R.layout.debug_analyze_result_detail);
        ListView listView = (ListView) findViewById(R.id.debug_analyze_result_detail_list);
        listView.setAdapter(new ResultDetailAdapter(this, (List<ActivityLaunchData>) getIntent().getSerializableExtra(EXTRAL_ACTIVITY_DATA)));
    }

    private class ResultDetailAdapter extends BaseArrayHolderAdapter<ActivityLaunchData>{

        public ResultDetailAdapter(Context context, List<ActivityLaunchData> data) {
            super(context, data);
        }

        @Override
        protected int getViewResId() {
            return R.layout.debug_analyze_result_detail_item;
        }

        @Override
        protected void bindDataToView(View convertView, ActivityLaunchData activityLaunchData, int position) {
            TextView layoutTime = findView(convertView, R.id.debug_analyze_result_detail_layout);
            layoutTime.setText(activityLaunchData.layoutTime + "");
            TextView drawTime = findView(convertView, R.id.debug_analyze_result_detail_draw);
            drawTime.setText(activityLaunchData.drawTime + "");
            TextView bizTime = findView(convertView, R.id.debug_analyze_result_detail_biz);
            bizTime.setText((activityLaunchData.totalTime - activityLaunchData.layoutTime - activityLaunchData.drawTime) + "");
            TextView totalTime = findView(convertView, R.id.debug_analyze_result_detail_total);
            totalTime.setText(activityLaunchData.totalTime + "");
        }
    }
}
