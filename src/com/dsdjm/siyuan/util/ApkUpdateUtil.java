package com.dsdjm.siyuan.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.dsdjm.siyuan.R;
import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ApkUpdateUtil {

    private ProgressDialog pd;
    private int fileSize;
    private int downLoadFileSize;
    private File file;
    private Context ctx;

    private static ApkUpdateUtil instance;

    private ApkUpdateUtil(Context ctx) {
        this.ctx = ctx;
    }

    public static ApkUpdateUtil newInstance(Context ctx) {
        if (instance == null) {
            instance = new ApkUpdateUtil(ctx);
        }
        return instance;
    }

    public void downloadApk(final String apkUrl, final String fileName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            pd = new ProgressDialog(ctx);
            pd.setMessage(ctx.getString(R.string.common_apk_downloaded));
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();

            new Thread() {
                public void run() {
                    try {
                        downFile(apkUrl, fileName);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            sendMsg(5);
        }
    }

    private void downFile(String apkUrl, String fileName) throws IOException {
        URL myURL = new URL(apkUrl);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        fileSize = conn.getContentLength();

        if (fileSize <= 0) {
            sendMsg(4);
            return;
        }
        if (is == null) {
            throw new RuntimeException(ctx.getString(R.string.common_stream_null));
        }
        if (AppUtil.getAvailableInternalSDCardSize() < this.fileSize) {
            sendMsg(3);
            return;
        }

        file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            sendMsg(0);

            do {
                int size = is.read(buf);
                if (size == -1) {
                    break;
                }
                fos.write(buf, 0, size);
                downLoadFileSize += size;

                sendMsg(1);
            } while (true);

            is.close();
            sendMsg(2);

        } catch (Exception ex) {
            Log.e("tag", "error: " + ex.getMessage(), ex);
        }
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        pd.setMax(fileSize);
                    case 1:
                        pd.setProgress(downLoadFileSize);
                        break;
                    case 2:
                        Toast.makeText(ctx, R.string.common_apk_downloaded,
                                Toast.LENGTH_SHORT).show();

                        pd.cancel();
                        install();
                        break;
                    case 3:
                        Toast.makeText(ctx, R.string.common_sdcard_empty,
                                Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(ctx, R.string.common_unknown_filesize,
                                Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(ctx, R.string.common_no_sdcard, Toast.LENGTH_LONG)
                                .show();
                        break;
                    case -1:
                        Toast.makeText(ctx, msg.getData().getString("error"),
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };

    private void install() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }
}
