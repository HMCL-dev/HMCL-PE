package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildVisibilityAdapter;

import java.util.ArrayList;

public class ChildVisibilityDialog extends Dialog implements View.OnClickListener {

    private String pattern;
    private ArrayList<String> list;
    private OnChildVisibilityChangeListener onChildVisibilityChangeListener;

    private ListView listView;
    private Button positive;

    public ChildVisibilityDialog(@NonNull Context context,String pattern, ArrayList<String> list,OnChildVisibilityChangeListener onChildVisibilityChangeListener) {
        super(context);
        this.pattern = pattern;
        this.list = list;
        this.onChildVisibilityChangeListener = onChildVisibilityChangeListener;
        setContentView(R.layout.dialog_child_visibility);
        setCancelable(false);
        init();
    }

    private void init(){
        listView = findViewById(R.id.child_list);
        positive = findViewById(R.id.exit);

        positive.setOnClickListener(this);

        ChildVisibilityAdapter adapter = new ChildVisibilityAdapter(getContext(),list,pattern,this);
        listView.setAdapter(adapter);
    }

    public void changeChildList(String name,boolean add){
        if (add) {
            list.add(name);
        }
        else {
            list.remove(name);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            onChildVisibilityChangeListener.onChildVisibilityChange(list);
            dismiss();
        }
    }

    public interface OnChildVisibilityChangeListener{
        void onChildVisibilityChange(ArrayList<String> list);
    }
}
