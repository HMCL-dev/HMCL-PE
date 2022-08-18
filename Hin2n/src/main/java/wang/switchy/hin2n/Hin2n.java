package wang.switchy.hin2n;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import wang.switchy.hin2n.storage.db.base.DaoMaster;
import wang.switchy.hin2n.storage.db.base.DaoSession;


/**
 * Created by janiszhang on 2018/4/19.
 */

public class Hin2n {

    public Context appContext;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    static {
        System.loadLibrary("slog");
        System.loadLibrary("uip");
        System.loadLibrary("n2n_v2s");
        // n2n_v2 is part of edge_v2 due to dependency on the g_status
        System.loadLibrary("n2n_v1");
        System.loadLibrary("edge_v2s");
        System.loadLibrary("edge_v2");
        System.loadLibrary("edge_v1");
        System.loadLibrary("edge_jni");
    }

    //静态单例
    public static Hin2n instance;

    public static Hin2n getInstance() {
        if (instance == null) {
            instance = new Hin2n();
        }
        return instance;
    }

    public void setup(Context context) {
        appContext = context;
        setDatabase();
        initNotificationChannel();
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(appContext, "N2N-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void initNotificationChannel() {
        String id = appContext.getString(R.string.notification_channel_id_default);
        String name = appContext.getString(R.string.notification_channel_name_default);
        createNotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
    }

    private void createNotificationChannel(String id, CharSequence name, int importance) {
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(id, name, importance));
    }
}
