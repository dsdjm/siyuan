package com.dsdjm.siyuan.util;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class AppUtil {

    /**
     * 获取手机可使用内存空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取设备的总内存空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机可使用SDCard空间
     *
     * @return
     */
    public static long getAvailableInternalSDCardSize() {
        if (IsAvailableSDCard()) {
            final File path = Environment.getExternalStorageDirectory();
            final StatFs stat = new StatFs(path.getPath());
            final long blockSize = stat.getBlockSize();
            final long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        return 0;
    }

    /**
     * 获取设备的SDCard总空间
     *
     * @return
     */
    public static long getTotalInternalSDCardSize() {
        if (IsAvailableSDCard()) {
            final File path = Environment.getExternalStorageDirectory();
            final StatFs stat = new StatFs(path.getPath());
            final long blockSize = stat.getBlockSize();
            final long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        return 0;
    }

    /**
     * 获取SDCard的状态,检验SDCard是否可用具有可读可写权限
     *
     * @return
     */
    public static boolean IsAvailableSDCard() {
        // SD卡在手机上正常使用状态
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 返回设备的唯一ID标识
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (RuntimeException e) {
            Log.w("device exception:", "Couldn't retrieve DeviceId for : "
                    + context.getPackageName(), e);
            return null;
        }
    }

    /**
     * 返回当前手机号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (RuntimeException e) {
            Log.w("device exception:", "Couldn't retrieve DeviceId for : "
                    + context.getPackageName(), e);
            return null;
        }
    }

    /**
     * 获取移动设备屏幕显示信息
     * @param act
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Activity act){
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取应用程序的文件绝对路径
     *
     * @param context
     * @return
     */
    public static String getApplicationFilePath(Context context) {
        final File filesDir = context.getFilesDir();
        if (filesDir != null) {
            return filesDir.getAbsolutePath();
        }

        Log.w("application exception:",
                "Couldn't retrieve ApplicationFilePath for : "
                        + context.getPackageName());
        return "Couldn't retrieve ApplicationFilePath";
    }

    /**
     * 返回设备的屏幕密度信息
     *
     * @param context
     * @return
     */
    public static String getDisplayDetails(Context context) {
        try {
            final WindowManager windowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            final Display display = windowManager.getDefaultDisplay();
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            final StringBuilder result = new StringBuilder();
            result.append("width=").append(display.getWidth()).append('\n');
            result.append("height=").append(display.getHeight()).append('\n');
            result.append("pixelFormat=").append(display.getPixelFormat())
                    .append('\n');
            result.append("refreshRate=").append(display.getRefreshRate())
                    .append("fps").append('\n');
            result.append("metrics.density=x").append(metrics.density)
                    .append('\n');
            result.append("metrics.scaledDensity=x")
                    .append(metrics.scaledDensity).append('\n');
            result.append("metrics.widthPixels=").append(metrics.widthPixels)
                    .append('\n');
            result.append("metrics.heightPixels=").append(metrics.heightPixels)
                    .append('\n');
            result.append("metrics.xdpi=").append(metrics.xdpi).append('\n');
            result.append("metrics.ydpi=").append(metrics.ydpi);
            return result.toString();

        } catch (RuntimeException e) {
            Log.w("display exception:",
                    "Couldn't retrieve DisplayDetails for : "
                            + context.getPackageName(), e);
            return "Couldn't retrieve Display Details";
        }
    }

    /**
     * 检查手机联网状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo network : info) {
                    if (network.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取当前应用版本号
     *
     * @param activity
     * @return
     */
    public static int getVersionCode(Activity activity) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前应用版本名称
     * @param context
     * @return
     */
    private static String getApkVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本信息
     * @param context
     * @return
     */
    private static String[] getMarketInfo(Context context){
        String versionName = getApkVersionName(context);
        if(isEmpty(versionName))
            return null;
        else
            return versionName.split("_");
    }

    /**
     * 获取市场编号
     * @param context
     * @return
     */
    public static String getMarketCode(Context context){
        String[] version = getMarketInfo(context);
        if(version==null || version.length<=0)
            return "";
        else
            return version[1];
    }

    /**
     * 获取版本名称
     * @param context
     * @return
     */
    public static String getVersionName(Context context){
        String[] version = getMarketInfo(context);
        if(version==null || version.length<=0)
            return "";
        else
            return version[0];
    }

    /**
     * 判断对象是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**
     * 获取当前使用的网络类型
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        try {
            // 获取系统的连接服务
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取网络的连接情况
            NetworkInfo activeNetInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetInfo != null) {
                return activeNetInfo.getTypeName();
            }
        } catch (RuntimeException e) {
            Log.w("device exception:", "Couldn't retrieve networkType for : "
                    + context.getPackageName(), e);
        }
        return "";
    }

    /**
     * 获取应用程序元数据Meta-Data
     * @param activity
     * @param key
     * @return
     */
    public static String getApplicationForMetaData(Activity activity,String key){
        try{
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData.getString(key);
        }catch(PackageManager.NameNotFoundException ex){
            Log.e("metadata exception", "Failed to load meta-data, NameNotFound: "
                    + ex.getMessage());
        }catch(NullPointerException  ex){
            Log.e("metadata exception", "Failed to load meta-data, NullPointer: "
                    + ex.getMessage());
        }
        return "";
    }

    /**
     * 缩放图片大小
     *
     * @param w
     *            宽
     * @param h
     *            高
     * @return
     */
    public static Drawable zoomDrawable(Bitmap bitmap,int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    public static <T> T softLoadResource(T _t) {
        SoftReference<T> softReference = new SoftReference<T>(_t);
        _t = null;

        return softReference.get();
    }

    public static <T> T weakLoadResource(T _t) {
        WeakReference<T> weakReference = new WeakReference<T>(_t);
        _t = null;

        return weakReference.get();
    }
}
