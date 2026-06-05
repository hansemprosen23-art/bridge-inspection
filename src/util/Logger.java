package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简单日志工具类
 * 支持控制台输出和文件记录
 */
public class Logger {

    private static final String LOG_DIR = "logs";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void info(String msg) {
        log("INFO", msg, null);
    }

    public static void warn(String msg) {
        log("WARN", msg, null);
    }

    public static void error(String msg, Exception e) {
        log("ERROR", msg, e);
    }

    public static void error(String msg) {
        log("ERROR", msg, null);
    }

    public static void debug(String msg) {
        log("DEBUG", msg, null);
    }

    private static synchronized void log(String level, String msg, Exception e) {
        String time = TIME_FORMAT.format(new Date());
        String logLine = "[" + time + "][" + level + "] " + msg;

        // 控制台输出
        if ("ERROR".equals(level)) {
            System.err.println(logLine);
            if (e != null) e.printStackTrace();
        } else {
            System.out.println(logLine);
        }

        // 写入日志文件
        try {
            String fileName = LOG_DIR + "/app_" + DATE_FORMAT.format(new Date()) + ".log";
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
                pw.println(logLine);
                if (e != null) {
                    e.printStackTrace(pw);
                }
            }
        } catch (IOException ioException) {
            System.err.println("日志写入失败: " + ioException.getMessage());
        }
    }
}
