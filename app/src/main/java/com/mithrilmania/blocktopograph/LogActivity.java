package com.mithrilmania.blocktopograph;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class LogActivity {
    public static final String path = Environment.getExternalStorageDirectory().getPath();
    public static final File logFile = new File(path, "blocktopograph.log");
    public static FileWriter fw = null;
    public static final LinkedList<String> logList = new LinkedList<>();

    private static String prependClassName(@NonNull Class caller, @NonNull String msg) {
        return caller.getName() + ": " + msg;
    }

    public static void closeFile() {
        logSyncToFile();
        try {
            fw.append("END\n");
            fw.close();
        } catch (Exception ignored) { }
    }

    public static void logSyncToFile() {
        try {
            StringBuilder text = new StringBuilder();
            for (String log: logList) {
                text.append(log).append("\n");
            }
            if (text.toString().length() != 0) fw.append(text.toString());
        } catch (Exception ignore) { }
    }

    public static void logInfo(@NonNull Class caller, @NonNull String msg) {
        logList.add("INFO " + prependClassName(caller, msg));
        logSyncToFile();
    }

    public static void logWarn(@NonNull Class caller, @NonNull String msg) {
        logList.add("WARN " + prependClassName(caller, msg));
        logSyncToFile();
    }

    public static void logWarn(@NonNull Class caller, @NonNull Throwable throwable) {
        logList.add("WARN " + prependClassName(caller, throwable.toString()));
        logSyncToFile();
    }

    public static void logError(@NonNull Class caller, @NonNull String msg) {
        logList.add("ERROR " + prependClassName(caller, msg));
        logSyncToFile();
    }

    public static void logError(@NonNull Class caller, @NonNull Throwable throwable) {
        logList.add("ERROR " + prependClassName(caller, throwable.toString()));
        logSyncToFile();
    }

    public static void cleanFile() {
        try {
            if (!logFile.exists()) logFile.createNewFile();
            fw = new FileWriter(logFile);
            fw.write("START\n");
        } catch (Exception ignored) { }
    }
}
