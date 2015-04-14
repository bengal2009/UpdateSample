package com.example.blin.updatesample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private static final String TAG = "Update";
    public ProgressDialog pBar;
    private Handler handler = new Handler();

    private int newVerCode = 0;
    private String newVerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getServerVerCode()) {
            int vercode = Config.getVerCode(this);
            if (newVerCode > vercode) {
                doNewVersionUpdate();
            } else {
                notNewVersionShow();
            }
        }

    }

    private boolean getServerVerCode() {
        try {
            String verjson = NetworkTool.getContent(Config.UPDATE_SERVER
                    + Config.UPDATE_VERJSON);
            JSONArray array = new JSONArray(verjson);
            if (array.length() > 0) {
                JSONObject obj = array.getJSONObject(0);
                try {
                    newVerCode = Integer.parseInt(obj.getString("verCode"));
                    newVerName = obj.getString("verName");
                } catch (Exception e) {
                    newVerCode = -1;
                    newVerName = "";
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void notNewVersionShow() {
        int verCode = Config.getVerCode(this);
        String verName = Config.getVerName(this);
        StringBuffer sb = new StringBuffer();
        sb.append("褰撳墠鐗堟湰:");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(",\n宸叉槸鏈€鏂扮増,鏃犻渶鏇存柊!");
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("杞欢鏇存柊").setMessage(sb.toString())// 璁剧疆鍐呭
                .setPositiveButton("纭畾",// 璁剧疆纭畾鎸夐挳
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }

                        }).create();// 鍒涘缓
        // 鏄剧ず瀵硅瘽妗�
        dialog.show();
    }

    private void doNewVersionUpdate() {
        int verCode = Config.getVerCode(this);
        String verName = Config.getVerName(this);
        StringBuffer sb = new StringBuffer();
        sb.append("褰撳墠鐗堟湰:");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(", 鍙戠幇鏂扮増鏈�:");
        sb.append(newVerName);
        sb.append(" Code:");
        sb.append(newVerCode);
        sb.append(", 鏄惁鏇存柊?");
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("杞欢鏇存柊")
                .setMessage(sb.toString())
                        // 璁剧疆鍐呭
                .setPositiveButton("鏇存柊",// 璁剧疆纭畾鎸夐挳
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                pBar = new ProgressDialog(MainActivity.this);
                                pBar.setTitle("姝ｅ湪涓嬭浇");
                                pBar.setMessage("璇风◢鍊�...");
                                pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                downFile(Config.UPDATE_SERVER
                                        + Config.UPDATE_APKNAME);
                            }

                        })
                .setNegativeButton("鏆備笉鏇存柊",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // 鐐瑰嚮"鍙栨秷"鎸夐挳涔嬪悗閫€鍑虹▼搴�
                                finish();
                            }
                        }).create();// 鍒涘缓
        // 鏄剧ず瀵硅瘽妗�
        dialog.show();
    }

    void downFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {

                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                Config.UPDATE_SAVENAME);
                        fileOutputStream = new FileOutputStream(file);

                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                            }
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });

    }

    void update() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

}