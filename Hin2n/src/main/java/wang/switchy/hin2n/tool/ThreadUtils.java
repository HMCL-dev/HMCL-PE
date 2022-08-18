package wang.switchy.hin2n.tool;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private final static ExecutorService cachedThreadExecutor = Executors.newCachedThreadPool();
    private final static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void cachedThreadExecutor(Runnable runnable){
        cachedThreadExecutor.execute(runnable);
    }

    public static void mainThreadExecutor(Runnable runnable){
        mHandler.post(runnable);
    }
}
