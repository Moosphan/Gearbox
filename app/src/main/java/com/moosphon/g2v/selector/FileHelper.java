package com.moosphon.g2v.selector;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class FileHelper {
    public static final String DEFAULT_SUB_DIR = "/Timeory/boxing";

    public static boolean createFile(String path) throws ExecutionException, InterruptedException {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        final File file = new File(path);
        return file.exists() || file.mkdirs();

    }

    @Nullable
    public static String getCacheDir(@NonNull Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        File cacheDir = context.getCacheDir();
        if (cacheDir == null) {
            Log.d("FileHelper","cache dir do not exist.");
            return null;
        }
        String result = cacheDir.getAbsolutePath() + "/boxing";
        try {
            FileHelper.createFile(result);
        } catch (ExecutionException | InterruptedException e) {
            Log.d("FileHelper", "cache dir " + result + " not exist");
            return null;
        }
        Log.d("FileHelper", "cache dir is: " + result);
        return result;
    }

    @Nullable
    public static String getBoxingPathInDCIM() {
        return getExternalDCIM(null);
    }

    @Nullable
    public static String getExternalDCIM(String subDir) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (file == null) {
                return null;
            }
            String dir = "/bili/boxing";
            if (!TextUtils.isEmpty(subDir)) {
                dir = subDir;
            }
            String result = file.getAbsolutePath() + dir;
            Log.d("FileHelper", "external DCIM is: " + result);
            return result;
        }
        Log.d("FileHelper", "external DCIM do not exist.");
        return null;
    }

    public static boolean isFileValid(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return isFileValid(file);
    }

    static boolean isFileValid(File file) {
        return file != null && file.exists() && file.isFile() && file.length() > 0 && file.canRead();
    }
}
