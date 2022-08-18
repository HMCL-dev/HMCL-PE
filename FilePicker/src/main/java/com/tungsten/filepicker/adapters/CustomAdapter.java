package com.tungsten.filepicker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.R;
import com.tungsten.filepicker.fileoperations.FileResolution;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.AssortedUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    public void selectAll() {
        for(int i=0;i<fileList.size();i++) {
            filteredfileList.get(i).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        for(int i=0;i<fileList.size();i++) {
            filteredfileList.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void selectItem(int position) {
        filteredfileList.get(position).setSelected(true);
        notifyDataSetChanged();
    }

    private List<FileItem> fileList;
    private List<FileItem> filteredfileList;

    private Constants.CHOICE_MODE currMode;
    private Context mContext;
    private FileFilter mFileFilter;
    @Override
    public Filter getFilter() {
        if (mFileFilter == null) {
            mFileFilter = new FileFilter();
        }
        return mFileFilter;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return Character.toString(filteredfileList.get(position).getFile().getName().charAt(0)).toUpperCase();
    }

    private class FileFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<FileItem> tempList = new ArrayList<FileItem>();
                // search content in friend list
                for (FileItem fileItem : fileList) {
                    if (fileItem.getFile().getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(fileItem);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = fileList.size();
                filterResults.values = fileList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredfileList = (ArrayList<FileItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName;
        public TextView fileModified;
        public ImageView fileIcon;
        public CheckBox selectcb;

        public MyViewHolder(View view) {
            super(view);
            fileName = (TextView) view.findViewById(R.id.filename);
            fileModified = (TextView) view.findViewById(R.id.filemodifiedinfo);
            fileIcon = (ImageView) view.findViewById(R.id.file_icon);
            selectcb = (CheckBox) view.findViewById(R.id.selectFile);
        }
    }


    public CustomAdapter(List<FileItem> fileList,Context mContext) {
        this.fileList = fileList;
        this.filteredfileList = fileList;
        this.currMode = Constants.CHOICE_MODE.SINGLE_CHOICE;
        this.mContext = mContext;
    }

    public void setChoiceMode(Constants.CHOICE_MODE mode) {
        this.currMode = mode;
        if(mode== Constants.CHOICE_MODE.SINGLE_CHOICE)
            for(FileItem item : filteredfileList) {
                item.setSelected(false);
            }
    }

    public Constants.CHOICE_MODE getChoiceMode() {
        return this.currMode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        File f = filteredfileList.get(holder.getAdapterPosition()).getFile();
        holder.fileIcon.setImageResource(FileResolution.getFileIcon(f));
        int length = 0;
        String children = "";
        if(f.isDirectory()) {
            if(f.listFiles()!=null)
                length = f.listFiles().length;
            children = " (" +length + ")";
        }

        holder.fileName.setText(f.getName() + children);
        try {
            Date d = new Date(f.lastModified());
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
            String fileSize = "";
            if(AssortedUtils.GetPrefs(Constants.SHOW_FOLDER_SIZE,mContext).equalsIgnoreCase("true")) {
                fileSize = filteredfileList.get(holder.getAdapterPosition()).getFileSize();
            }
            holder.fileModified.setText(mContext.getString(R.string.file_info,fileSize,formatter.format(d)));
        } catch (Exception e) {

        }
        if(getChoiceMode()== Constants.CHOICE_MODE.MULTI_CHOICE) {
            holder.selectcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    filteredfileList.get(holder.getAdapterPosition()).setSelected(isChecked);
                }
            });
            holder.selectcb.setChecked(filteredfileList.get(holder.getAdapterPosition()).isSelected());
        } else {
            holder.selectcb.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredfileList.size();
    }

    public List<FileItem> getSelectedItems() {
        List<FileItem> selectedItems = new ArrayList<FileItem>();
        if(getChoiceMode()== Constants.CHOICE_MODE.MULTI_CHOICE) {
            for(FileItem item : filteredfileList) {
                if(item.isSelected())
                    selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public FileItem getItemAt(int position) {
        return filteredfileList.get(position);
    }
}