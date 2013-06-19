package com.dsdjm.siyuan.util;

import android.util.Log;
import com.dsdjm.siyuan.MainConfig;

public class LogUtil {

    private static boolean on = MainConfig.SHOW_LOG;

    public static void debug(String tag, String msg) {
        if (on) {
            Log.d(tag, msg);
        }
    }

    public static void error(String tag, String msg) {
        if (on) {
            Log.e(tag, msg);
        }
    }

    public static void info(String tag, String msg) {
        if (on) {
            Log.i(tag, msg);
        }
    }

    public static void warn(String tag, String msg) {
        if (on) {
            Log.w(tag, msg);
        }
    }

    public static void print(String msg) {
        if (on) {
            System.out.print(msg);
        }
    }

    public static void println(String msg) {
        if (on) {
            System.out.println(msg);
        }
    }

    public static void exception(String msg) {
        if (on) {
            System.out.println(msg);
        }
    }

    public static void exception(String msg, Exception e) {
        if (on) {
            System.out.println(msg);
            e.printStackTrace();
        }
    }
}
