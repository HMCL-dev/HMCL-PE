package com.tungsten.hmclpe.launcher.list.account.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;

import java.util.ArrayList;

public class AuthlibInjectorServerSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AuthlibInjectorServer> list;

    private class ViewHolder{
        TextView name;
        TextView url;
    }

    public AuthlibInjectorServerSpinnerAdapter (Context context,ArrayList<AuthlibInjectorServer> list) {
        this.context = context;
        this.list = list;
    }

    public int getItemPosition(AuthlibInjectorServer authlibInjectorServer){
        for (int i = 0;i < list.size();i++){
            if (list.get(i).equals(authlibInjectorServer)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_server_list,null);
            viewHolder.name = convertView.findViewById(R.id.server_name);
            viewHolder.url = convertView.findViewById(R.id.server_url);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        AuthlibInjectorServer authlibInjectorServer = list.get(position);
        viewHolder.name.setText(authlibInjectorServer.getName());
        viewHolder.url.setText(authlibInjectorServer.getUrl());
        return convertView;
    }
}
