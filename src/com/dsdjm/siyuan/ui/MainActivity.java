package com.dsdjm.siyuan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.waps.AppConnect;
import com.dsdjm.siyuan.MainConfig;
import com.dsdjm.siyuan.MainConst;
import com.dsdjm.siyuan.MainStatic;
import com.dsdjm.siyuan.R;
import com.dsdjm.siyuan.model.Group;
import com.dsdjm.siyuan.util.HttpUtil;
import com.dsdjm.siyuan.util.JSonUtil;
import com.example.android.bitmapfun.ui.ImageDetailActivity;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    protected LayoutInflater mInflater;

    private ListView mlistView;

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
                    for (Group g : groups) {
                        MainStatic.groupList.add(g);
                    }
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

        AppConnect.getInstance(this);

        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.activity_main);

        mlistView = (ListView) findViewById(android.R.id.list);
        mlistView.setAdapter(mListAdapter);
        mlistView.setOnItemClickListener(this);

        task.execute();
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
}
