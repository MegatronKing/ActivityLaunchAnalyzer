package com.activity.analyzer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.analyzer.library.ActivityLaunchData;
import com.activity.analyzer.library.AnalyzerDataHolder;
import com.activity.analyzer.library.LevelToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 分析结果页
 *
 * @author Megatron King
 * @since 2016/2/22 15:24
 */
public class AnalyzerResultActivity extends ListActivity {

    private List<ActivityStat> mAllPageStat = new ArrayList<ActivityStat>();
    private List<ActivityStat> mNormalPageStat = new ArrayList<ActivityStat>();
    private List<ActivityStat> mSlightPageStat = new ArrayList<ActivityStat>();
    private List<ActivityStat> mWarningPageStat = new ArrayList<ActivityStat>();
    private List<ActivityStat> mAlterPageStat = new ArrayList<ActivityStat>();
    private List<ActivityStat> mFatalPageStat = new ArrayList<ActivityStat>();

    private MenuItem mMenuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Analyzer Result");
        analyzerData();
    }

    private void analyzerData(){
        Map<String, List<ActivityLaunchData>> dataHolder = AnalyzerDataHolder.get();
        if(dataHolder == null || dataHolder.isEmpty()){
            Toast.makeText(this, "No Stat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            for (String componentName : dataHolder.keySet()){
                if(componentName.equals(getClass().getName()) || componentName.equals(AnalyzerResultDetailActivity.class.getName())){
                    continue;
                }
                List<ActivityLaunchData> activityLaunchData = dataHolder.get(componentName);
                long totalLaunchTime = 0;
                for (ActivityLaunchData data : activityLaunchData){
                    totalLaunchTime += data.thisTime;
                }
                ActivityStat activityStat = new ActivityStat();
                activityStat.componentName = activityLaunchData.get(0).getPageShortName();
                activityStat.statCount = activityLaunchData.size();
                activityStat.launchTime = totalLaunchTime / activityStat.statCount;
                activityStat.data = new ArrayList<ActivityLaunchData>(activityLaunchData);
                activityStat.level = LevelToast.getLevel(activityStat.launchTime);
                mAllPageStat.add(activityStat);
            }
            Collections.sort(mAllPageStat, new Comparator<ActivityStat>() {
                @Override
                public int compare(ActivityStat lhs, ActivityStat rhs) {
                    return (int) (lhs.launchTime - rhs.launchTime);
                }
            });
            for (ActivityStat activityStat : mAllPageStat){
                if(activityStat.level == LevelToast.LEVEL_NORMAL){
                    mNormalPageStat.add(activityStat);
                }
                if(activityStat.level == LevelToast.LEVEL_SLIGHT){
                    mSlightPageStat.add(activityStat);
                }
                if(activityStat.level == LevelToast.LEVEL_WARNING){
                    mWarningPageStat.add(activityStat);
                }
                if(activityStat.level == LevelToast.LEVEL_ALERT){
                    mAlterPageStat.add(activityStat);
                }
                if(activityStat.level == LevelToast.LEVEL_FATAL){
                    mFatalPageStat.add(activityStat);
                }
            }
        }
        setListAdapter(new ResultListAdapter(this, mAllPageStat));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ActivityStat activityStat = (ActivityStat) getListAdapter().getItem(position);
        Intent intent = new Intent(this, AnalyzerResultDetailActivity.class);
        intent.putExtra(AnalyzerResultDetailActivity.EXTRAL_ACTIVITY_NAME, activityStat.componentName);
        intent.putExtra(AnalyzerResultDetailActivity.EXTRAL_ACTIVITY_DATA, activityStat.data);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.debug_analyzer_result, menu);
        menu.findItem(R.id.action_normal).setVisible(!mNormalPageStat.isEmpty());
        menu.findItem(R.id.action_slight).setVisible(!mSlightPageStat.isEmpty());
        menu.findItem(R.id.action_warning).setVisible(!mWarningPageStat.isEmpty());
        menu.findItem(R.id.action_alert).setVisible(!mAlterPageStat.isEmpty());
        menu.findItem(R.id.action_fatal).setVisible(!mFatalPageStat.isEmpty());
        mMenuBar = menu.findItem(R.id.action_menu_bar);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListAdapter adapter = null;
        switch (item.getItemId()){
            case R.id.action_menu_bar:
                return true;
            case R.id.action_all:
                adapter = new ResultListAdapter(this, mAllPageStat);
                mMenuBar.setTitle("All");
                break;
            case R.id.action_normal:
                adapter = new ResultListAdapter(this, mNormalPageStat);
                mMenuBar.setTitle("Perfect");
                break;
            case R.id.action_slight:
                adapter = new ResultListAdapter(this, mSlightPageStat);
                mMenuBar.setTitle("Normal");
                break;
            case R.id.action_warning:
                adapter = new ResultListAdapter(this, mWarningPageStat);
                mMenuBar.setTitle("Slow");
                break;
            case R.id.action_alert:
                adapter = new ResultListAdapter(this, mAlterPageStat);
                mMenuBar.setTitle("Serious");
                break;
            case R.id.action_fatal:
                adapter = new ResultListAdapter(this, mFatalPageStat);
                mMenuBar.setTitle("Fatal");
                break;
        }
        setListAdapter(adapter);
        return true;
    }

    private class ResultListAdapter extends BaseArrayHolderAdapter<ActivityStat>{

        public ResultListAdapter(Context context, List<ActivityStat> data) {
            super(context, data);
        }

        @Override
        protected int getViewResId() {
            return R.layout.debug_analyze_result_item;
        }

        @Override
        protected void bindDataToView(View convertView, ActivityStat activityLaunchData, int position) {
            TextView page = findView(convertView, R.id.debug_analyze_result_page);
            TextView time = findView(convertView, R.id.debug_analyze_result_time);
            page.setText(activityLaunchData.componentName);
            time.setText("average time：" + activityLaunchData.launchTime + "ms   stat count：" + activityLaunchData.statCount);

            ColorDrawable drawable = new ColorDrawable(LevelToast.getColorByLevel(activityLaunchData.level));
            drawable.setBounds(0, 0, 30, 30);
            page.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private class ActivityStat implements Serializable {
        private String componentName;
        private long launchTime;
        private int statCount;
        private int level;
        private ArrayList<ActivityLaunchData> data;
    }
}
