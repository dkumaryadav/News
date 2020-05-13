package com.deepakyadav.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ColorsAdapter extends BaseAdapter{

    ArrayList<Drawer> arrayList;
    Context context;

    public ColorsAdapter(Context context, ArrayList<Drawer> list) {
        this.arrayList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = (LayoutInflater.from(context).inflate(R.layout.drawer_list_item, parent, false));

        Drawer drawer = arrayList.get(position);
        TextView textView = convertView.findViewById(R.id.listItem);
        textView.setText( drawer.getName() );
        return convertView;
    }

}
