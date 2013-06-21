package com.dsdjm.siyuan.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import cn.waps.AdView;
import cn.waps.AppConnect;
import cn.waps.MiniAdView;
import cn.waps.extend.QuitPopAd;
import cn.waps.extend.SlideWall;
import com.dsdjm.siyuan.MainConfig;
import com.dsdjm.siyuan.MainConst;
import com.dsdjm.siyuan.MainStatic;
import com.dsdjm.siyuan.R;
import com.dsdjm.siyuan.model.Group;
import com.dsdjm.siyuan.model.Item;
import com.dsdjm.siyuan.util.HttpUtil;
import com.dsdjm.siyuan.util.JSonUtil;
import com.example.android.bitmapfun.ui.ImageDetailActivity;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private LayoutInflater mInflater;
    private SharedPreferences mPreferences;

    private ListView mlistView;
    // 抽屉广告布局
    private View slidingDrawerView;

    private AsyncTask task = new AsyncTask() {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                String result = HttpUtil.get(MainConfig.URL_GET_DETAILS);

                int start = result.indexOf(MainConst.TAG_START);
                int end = result.indexOf(MainConst.TAG_END);
                result = result.substring(start + 4, end);
                Group[] groups = JSonUtil.parseArray(result, Group.class);
                MainStatic.groupList.clear();
                if (groups != null && groups.length > 0) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString(MainConst.PREFERENCE_GROUP, result);
                    editor.commit();
                    parseGroups(groups);
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mListAdapter.notifyDataSetChanged();
        }
    };


    private BaseAdapter mListAdapter = new BaseAdapter() {
        public int getCount() {
            if (null == MainStatic.groupList) return 0;
            else return MainStatic.groupList.size();
        }

        public Object getItem(int position) {
            return MainStatic.groupList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_main_item, null, false);
                holder = new ViewHolder();
                holder.titleTV = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.titleTV.setText(MainStatic.groupList.get(position).title);

            return convertView;
        }

        class ViewHolder {
            TextView titleTV;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AppConnect.getInstance(this);

        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        mlistView = (ListView) findViewById(android.R.id.list);
        mlistView.setAdapter(mListAdapter);
        mlistView.setOnItemClickListener(this);

        if (mPreferences.contains(MainConst.PREFERENCE_GROUP)) {
            Group[] groups = JSonUtil.parseArray(mPreferences.getString(MainConst.PREFERENCE_GROUP, ""), Group.class);
            if (groups != null && groups.length > 0) {
                parseGroups(groups);
            }
        }
        task.execute();

        // 抽屉式应用墙
        // 1,将drawable-hdpi文件夹中的图片全部拷贝到新工程的drawable-hdpi文件夹中
        // 2,将layout文件夹中的detail.xml和slidewall.xml两个文件，拷贝到新工程的layout文件夹中
        // 获取抽屉样式的自定义广告
        slidingDrawerView = SlideWall.getInstance().getView(this);
        // 获取抽屉样式的自定义广告,自定义handle距左边边距为150
        // slidingDrawerView = SlideWall.getInstance().getView(this, 150);
        // 获取抽屉样式的自定义广告,自定义列表中每个Item的宽度480,高度150
        // slidingDrawerView = SlideWall.getInstance().getView(this, 480, 150);
        // 获取抽屉样式的自定义广告,自定义handle距左边边距为150,列表中每个Item的宽度480,高度150
        // slidingDrawerView = SlideWall.getInstance().getView(this, 150, 480, 150);

        if (slidingDrawerView != null) {
            this.addContentView(slidingDrawerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        }

        // 迷你广告调用方式
        // AppConnect.getInstance(this).setAdBackColor(Color.argb(50, 120, 240, 120));//设置迷你广告背景颜色
        // AppConnect.getInstance(this).setAdForeColor(Color.YELLOW);//设置迷你广告文字颜色
        LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
        new MiniAdView(this, miniLayout).DisplayAd(10);// 10秒刷新一次

        // 互动广告调用方式
        LinearLayout container = (LinearLayout) findViewById(R.id.AdLinearLayout);
        new AdView(this, container).DisplayAd();
    }

    private void parseGroups(Group[] groups) {
        for (Group g : groups) {
            if (g.length != 0 && g.start < g.end) {
                for (int i = g.start; i <= g.end; i++) {
                    String t = "" + i;
                    int dist = g.length - t.length();
                    while (dist > 0) {
                        t = "0" + t;
                        dist--;
                    }

                    if (g.prefix != null && !g.prefix.equals("")) {
                        t = g.prefix + t;
                    }

                    if (g.suffix != null && !g.suffix.equals("")) {
                        t = t + g.suffix;
                    }
                    g.items.add(new Item(t));
                }

            }
            MainStatic.groupList.add(g);
        }
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainStatic.index = position;
        final Intent i = new Intent(this, ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) 0);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(this).finalize();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SlideWall.getInstance().slideWallDrawer != null
                    && SlideWall.getInstance().slideWallDrawer.isOpened()) {

                // 如果抽屉式应用墙展示中，则关闭抽屉
                SlideWall.getInstance().closeSlidingDrawer();
            } else {
                // 调用退屏广告
                QuitPopAd.getInstance().show(this);
            }

        }
        return true;
    }

    //建议加入onConfigurationChanged回调方法
    //注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 横竖屏状态切换时,关闭处于打开状态中的退屏广告
        QuitPopAd.getInstance().close();
        // 使用抽屉式应用墙,横竖屏状态切换时,重新加载抽屉,保证ListView重新加载,保证ListView中Item的布局匹配当前屏幕状态
        if (slidingDrawerView != null) {
            // 先remove掉slidingDrawerView
            ((ViewGroup) slidingDrawerView.getParent()).removeView(slidingDrawerView);
            slidingDrawerView = null;
            // 重新获取抽屉样式布局,此时ListView重新设置了Adapter
            slidingDrawerView = SlideWall.getInstance().getView(this);
            if (slidingDrawerView != null) {
                this.addContentView(slidingDrawerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }
        }
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 用于监听插屏广告的显示与关闭
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
        if (dialog != null) {
            if (dialog.isShowing()) {
                // 插屏广告正在显示
            }
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // 监听插屏广告关闭事件
                }
            });
        }
    }
}
