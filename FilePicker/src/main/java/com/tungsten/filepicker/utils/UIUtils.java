package com.tungsten.filepicker.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.tungsten.filepicker.R;
import com.tungsten.filepicker.interfaces.IFuncPtr;

public class UIUtils {

	public static void ShowError(String msg, Context context)
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);                      
	    dlgAlert.setMessage(msg);
	    dlgAlert.setTitle(context.getString(R.string.error_common));
	    dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);
	    dlgAlert.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface popup, int arg1) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});
	    dlgAlert.setCancelable(false);
	    dlgAlert.show();
	}
	
	public static void ShowMsg(String msg, String title,Context context)
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);                      
	    dlgAlert.setMessage(msg);
	    dlgAlert.setTitle(title);
	    dlgAlert.setIcon(android.R.drawable.ic_dialog_info);
	    dlgAlert.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface popup, int arg1) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});
	    dlgAlert.setCancelable(false);
	    dlgAlert.show();
	}
	
	public static void ShowToast(String msg,Context context)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showRadioButtonDialog(Context mContext, String[] options, String title, final RadioGroup.OnCheckedChangeListener listener) {

		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.filter_options, null);

		// custom dialog
		final Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(title)
				.setView(v)
				.create();

		RadioGroup rg = (RadioGroup) v.findViewById(R.id.filter_group);
		BootstrapButton okButton = (BootstrapButton) v.findViewById(R.id.okbutton);
		for(int i=0;i<options.length;i++){
			RadioButton rb=new RadioButton(mContext); // dynamically creating RadioButton and adding to RadioGroup.
			rb.setText(options[i]);
			rb.setId(i);
			rg.addView(rb);
		}

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				listener.onCheckedChanged(radioGroup,i);
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}


	public static void showEditTextDialog(Context mContext, String title, String initialText, final IFuncPtr functionToBeRun) {

		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.dialog_with_text, null);

		// custom dialog
		final Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(title)
				.setView(v)
				.create();

		BootstrapButton okButton = (BootstrapButton) v.findViewById(R.id.okbutton);
		final BootstrapEditText insertedText = (BootstrapEditText) v.findViewById(R.id.addedText);
		insertedText.setText(initialText);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				functionToBeRun.execute(insertedText.getText().toString());
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
