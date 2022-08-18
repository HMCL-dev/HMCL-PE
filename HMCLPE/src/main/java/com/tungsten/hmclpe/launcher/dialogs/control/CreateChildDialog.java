package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildLayout;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class CreateChildDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String pattern;
    private OnChildAddListener onChildAddListener;

    private EditText editName;
    private Spinner spinner;

    private Button positive;
    private Button negative;

    private int visibility;

    public CreateChildDialog(@NonNull Context context,String pattern,OnChildAddListener onChildAddListener) {
        super(context);
        this.pattern = pattern;
        this.onChildAddListener = onChildAddListener;
        setContentView(R.layout.dialog_create_child);
        setCancelable(false);
        init();
    }

    private void init(){
        editName = findViewById(R.id.edit_child_name);
        spinner = findViewById(R.id.visibility_spinner);

        positive = findViewById(R.id.create_current_child);
        negative = findViewById(R.id.exit);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        ArrayList<String> list = new ArrayList<>();
        list.add(getContext().getString(R.string.dialog_create_child_visibility_visible));
        list.add(getContext().getString(R.string.dialog_create_child_visibility_invisible));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        ArrayList<ChildLayout> childLayouts = SettingUtils.getChildList(pattern);
        ArrayList<String> list = new ArrayList<>();
        for (ChildLayout childLayout : childLayouts){
            list.add(childLayout.name);
        }
        boolean exist = list.contains(editName.getText().toString());
        if (view == positive){
            if (editName.getText().toString().equals("")){
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_child_warn),Toast.LENGTH_SHORT).show();
            }
            else if (editName.getText().toString().equals("info")){
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_child_warn_info),Toast.LENGTH_SHORT).show();
            }
            else if (exist){
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_child_warn_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                onChildAddListener.onChildAdd(new ChildLayout(editName.getText().toString(),visibility,new ArrayList<>(),new ArrayList<>()));
                dismiss();
            }
        }
        if (view == negative){
            dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0){
            visibility = View.VISIBLE;
        }
        if (i == 1){
            visibility = View.INVISIBLE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnChildAddListener{
        void onChildAdd(ChildLayout childLayout);
    }

}
