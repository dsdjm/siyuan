package com.dsdjm.siyuan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dsdjm.siyuan.R;
import com.dsdjm.siyuan.model.Group;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    protected LayoutInflater mInflater;

    private ListView mlistView;
    private List<Group> mGroupList = new ArrayList<Group>();


    private ListAdapter mListAdapter = new BaseAdapter() {
        public int getCount() {
            if (null == mGroupList) return 0;
            else return mGroupList.size();
        }

        public Object getItem(int position) {
            return mGroupList.get(position);
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


            holder.titleTV.setText(mGroupList.get(position).title);

            return convertView;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(R.layout.activity_main);

        mlistView = (ListView) findViewById(android.R.id.list);
        mlistView.setAdapter(mListAdapter);
        mlistView.setOnItemClickListener(this);
    }

    private class ViewHolder {
        TextView titleTV;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
