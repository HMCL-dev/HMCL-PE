package com.tungsten.hmclpe.launcher.list.local.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.dialogs.control.ChildManagerDialog;
import com.tungsten.hmclpe.launcher.dialogs.control.EditChildDialog;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class ChildLayoutListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChildLayout> list;
    private ControlPattern controlPattern;
    private ChildManagerDialog childManagerDialog;

    public ChildLayoutListAdapter(Context context, ArrayList<ChildLayout> list,ControlPattern controlPattern,ChildManagerDialog childManagerDialog){
        this.context = context;
        this.list = list;
        this.controlPattern = controlPattern;
        this.childManagerDialog = childManagerDialog;
    }

    private class ViewHolder{
        TextView name;
        ImageButton edit;
        ImageButton delete;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_child_layout,null);
            viewHolder.name = view.findViewById(R.id.child_name);
            viewHolder.edit = view.findViewById(R.id.edit_child);
            viewHolder.delete = view.findViewById(R.id.delete_child);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ChildLayout childLayout = list.get(i);
        viewHolder.name.setText(childLayout.name);
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditChildDialog dialog = new EditChildDialog(context, controlPattern.name, new EditChildDialog.OnChildChangeListener() {
                    @Override
                    public void onChildChange(ChildLayout child) {
                        FileUtils.rename(AppManifest.CONTROLLER_DIR + "/" + controlPattern.name + "/" + childLayout.name + ".json",child.name + ".json");
                        ChildLayout.saveChildLayout(controlPattern.name,child);
                        childManagerDialog.refreshListView();
                    }
                },childLayout);
                dialog.show();
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.dialog_delete_child_title));
                builder.setMessage(context.getString(R.string.dialog_delete_child_content));
                builder.setPositiveButton(context.getString(R.string.dialog_delete_child_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int p) {
                        if (new File(AppManifest.CONTROLLER_DIR + "/" + controlPattern.name + "/" + childLayout.name + ".json").delete()){
                            childManagerDialog.refreshListView();
                        }
                    }
                });
                builder.setNegativeButton(context.getString(R.string.dialog_delete_child_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int p) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return view;
    }
}
