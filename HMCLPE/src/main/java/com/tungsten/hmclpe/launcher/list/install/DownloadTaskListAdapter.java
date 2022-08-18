package com.tungsten.hmclpe.launcher.list.install;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tungsten.hmclpe.R;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class DownloadTaskListAdapter extends RecyclerView.Adapter<DownloadTaskListAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<DownloadTaskListBean> list;

    private boolean checkMod = false;

    public DownloadTaskListAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setCheckMod(boolean checkMod) {
        this.checkMod = checkMod;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_task,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadTaskListBean downloadTaskListBean = list.get(position);
        holder.progressBar.setProgress(downloadTaskListBean.progress);
        holder.fileName.setText(downloadTaskListBean.name);
        if (checkMod || (!downloadTaskListBean.name.equals(context.getString(R.string.dialog_install_assets_check)) && !downloadTaskListBean.name.equals(context.getString(R.string.dialog_install_game_install_forge_build)) && (downloadTaskListBean.path == null || downloadTaskListBean.path.equals("")) && (downloadTaskListBean.url == null || downloadTaskListBean.url.equals("")) && (downloadTaskListBean.sha1 == null || downloadTaskListBean.sha1.equals("")))) {
            holder.progressBar.setIndeterminate(true);
        }
        else {
            holder.progressBar.setIndeterminate(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public DownloadTaskListBean getItem(int i) {
        return list.get(i);
    }

    public void addDownloadTask(DownloadTaskListBean bean) {
        list.add(bean);
        this.notifyItemInserted(list.size() - 1);
    }

    public void onProgress(DownloadTaskListBean bean) {
        for(int i = 0; i < list.size(); ++i) {
            if ((list.get(i).url.equals(bean.url) && !bean.url.equals("")) || (list.get(i).name.equals(bean.name) && bean.url.equals(""))) {
                list.set(i, bean);
                this.notifyItemChanged(i);
            }
        }
    }

    public void onComplete(DownloadTaskListBean bean) {
        for(int i = 0; i < list.size(); ++i) {
            if ((list.get(i).url.equals(bean.url) && !bean.url.equals("")) || (list.get(i).name.equals(bean.name) && bean.url.equals(""))) {
                list.remove(i);
                this.notifyItemRemoved(i);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView fileName;
        private final ProgressBar progressBar;

        public ViewHolder(View parent) {
            super(parent);
            this.progressBar = parent.findViewById(R.id.download_task_progress);
            this.fileName = parent.findViewById(R.id.download_task_name);
        }
    }
}
