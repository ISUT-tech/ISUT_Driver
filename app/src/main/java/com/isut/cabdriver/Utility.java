package com.isut.cabdriver;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Utility {
    public static String getTempMediaDirectory(Context context) {
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = context.getExternalCacheDir();
        } else {
            dir = context.getCacheDir();
        }

        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (dir.exists() && dir.isDirectory()) {
            return dir.getAbsolutePath();
        }
        return null;
    }
}
