package wang.switchy.hin2n.tool;

import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import wang.switchy.hin2n.event.LogChangeEvent;

public class LogFileObserver extends FileObserver {

    int action;
    String txtPath;
    public LogFileObserver(String path) {
        this(path,ALL_EVENTS);
    }

    public LogFileObserver(String path, int mask) {
        super(path, mask);
        this.txtPath = path;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        action = event & FileObserver.ALL_EVENTS;
        switch (action){
//            case FileObserver.ACCESS:
//                Log.d("LogFileObserver","event: 文件或目录被访问, path: " + path);
//                EventBus.getDefault().post(new LogChangeEvent(txtPath));
//                break;

            case FileObserver.DELETE:
                Log.d("LogFileObserver","event: 文件或目录被删除, path: " + path);
                EventBus.getDefault().post(new LogChangeEvent(txtPath));
                break;

//            case FileObserver.OPEN:
//                Log.d("LogFileObserver","event: 文件或目录被打开, path: " + path);
//                EventBus.getDefault().post(new LogChangeEvent(txtPath));
//                break;

            case FileObserver.MODIFY:
                Log.d("LogFileObserver","event: 文件或目录被修改, path: " + path);
                EventBus.getDefault().post(new LogChangeEvent(txtPath));
                break;

            case FileObserver.CREATE:
                Log.d("LogFileObserver","event: 文件或目录被创建, path: " + path);
                EventBus.getDefault().post(new LogChangeEvent(txtPath));
                break;
        }
    }
}
