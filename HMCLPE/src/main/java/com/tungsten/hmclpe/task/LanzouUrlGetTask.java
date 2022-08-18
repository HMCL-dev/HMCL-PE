package com.tungsten.hmclpe.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.tungsten.hmclpe.manifest.AppManifest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author ShirosakiMio
 */
public class LanzouUrlGetTask extends AsyncTask<String, Integer, String> {

    public interface Callback{
        void onStart();
        void onError(Exception e);
        void onFinish(String url);
    }

    private WeakReference<Activity> activity;
    private Callback callback;
    private String fianalUrl=null;
    private String UA="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36";

    public LanzouUrlGetTask(Activity activity, Callback callback) {
        this.activity = new WeakReference<>(activity);
        this.callback = callback;
    }

    @Override
    public void onPreExecute() {
        activity.get().runOnUiThread(() -> callback.onStart());
    }

    @Override
    public String doInBackground(String... args) {
        new File(AppManifest.DEBUG_DIR + "/lanzou_debug.txt").delete();
        try {
            String url = args[0];
            Document doc= null;
            doc = Jsoup.connect(url)
                    .userAgent(UA)
                    .get();
            Elements elements = doc.getElementsByClass("ifr2");
            for (Element element:elements){
                url = url.substring(0,url.indexOf(".com") + 4) + element.attr("src");
                String postUrl=url.substring(0,url.indexOf(".com")+4)+"/ajaxm.php";
                doc = Jsoup.connect(url)
                        .userAgent(UA)
                        .get();
                doc =Jsoup.parse(doc.toString().replaceAll("//[\\s\\S]*?\\n",""));
                OkHttpClient client=new OkHttpClient().newBuilder().followRedirects(false).build();
                Pattern p =null;
                p=Pattern.compile("vsign = '(.*?)'");
                Matcher m = p.matcher(doc.toString());
                m.find();
                String vsign=m.group(1);

                p=Pattern.compile("cwebsignkeyc = '(.*?)'");
                m = p.matcher(doc.toString());
                m.find();
                String cwebsignkeyc=m.group(1);

                p=Pattern.compile("awebsigna = '(.*?)'");
                m = p.matcher(doc.toString());
                m.find();
                String awebsigna=m.group(1);

                p =Pattern.compile("ajaxdata = '(.*?)'");
                m = p.matcher(doc.toString());
                m.find();
                String ajaxdata=m.group(1);
                RequestBody body=new FormBody.Builder()
                        .add("action","downprocess")
                        .add("signs",ajaxdata)
                        .add("sign",vsign)
                        .add("ves","1")
                        .add("websign",awebsigna)
                        .add("websignkey",cwebsignkeyc)
                        .build();
                Request request=new Request.Builder()
                        .url(postUrl)
                        .post(body)
                        .header("User-Agent",UA)
                        .header("referer",url)
                        .header("accept","application/json, text/javascript, */*")
                        .header("Accept-Language","zh-CN,zh;q=0.9")
                        .build();
                Call call=client.newCall(request);
                Response response=call.execute();
                String result=response.body().string();
                Log.e("LanzouUrlGetTask",result);
                writeLog(result);
                JSONObject jsonObject=new JSONObject(result);
                fianalUrl=jsonObject.getString("dom")+"/file/"+jsonObject.getString("url");
                Log.e("LanzouUrlGetTask",""+fianalUrl);
                writeLog(fianalUrl);
                request=new Request.Builder().url(fianalUrl)
                        .header("accept","application/json, text/javascript, */*")
                        .header("Accept-Language","zh-CN,zh;q=0.9")
                        .header("User-Agent",UA)
                        .get().build();
                response=client.newCall(request).execute();
                Log.e("LanzouUrlGetTask",""+response.headers().toString());
                writeLog(response.headers().toString());
                if (response.code()==302){
                    fianalUrl=response.headers().get("Location");
                }else {
                    return null;
                }
                Log.e("LanzouUrlGetTask",""+fianalUrl);
                writeLog(fianalUrl);
                return fianalUrl;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("测试",e.toString());
            activity.get().runOnUiThread(() -> callback.onError(e));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... p1) {

    }

    @Override
    public void onPostExecute(String result) {
        activity.get().runOnUiThread(() -> callback.onFinish(result));
    }
    private void writeLog(String log){
        FileWriter fw = null;
        try {
            //如果⽂件存在，则追加内容；如果⽂件不存在，则创建⽂件
            File f = new File(AppManifest.DEBUG_DIR + "/lanzou_debug.txt");
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(log);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
