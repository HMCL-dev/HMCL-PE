package com.tungsten.hmclpe.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * @author huangyuhui
 */
public final class Logging {
    private Logging() {
    }

    public static final Logger LOG = Logger.getLogger("HMCL");
    private static ByteArrayOutputStream storedLogs = new ByteArrayOutputStream();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void start(Path logFolder) {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);

        try {
            Files.createDirectories(logFolder);
            FileHandler fileHandler = new FileHandler(logFolder.resolve("hmclpe.log").toAbsolutePath().toString());
            fileHandler.setLevel(Level.FINEST);
            fileHandler.setFormatter(DefaultFormatter.INSTANCE);
            LOG.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Unable to create hmclpe.log, " + e.getMessage());
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(DefaultFormatter.INSTANCE);
        consoleHandler.setLevel(Level.FINER);
        LOG.addHandler(consoleHandler);

        StreamHandler streamHandler = new StreamHandler(storedLogs, DefaultFormatter.INSTANCE) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        try {
            streamHandler.setEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        streamHandler.setLevel(Level.ALL);
        LOG.addHandler(streamHandler);
    }

    public static void initForTest() {
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(DefaultFormatter.INSTANCE);
        consoleHandler.setLevel(Level.FINER);
        LOG.addHandler(consoleHandler);
    }

    public static byte[] getRawLogs() {
        return storedLogs.toByteArray();
    }

    public static String getLogs() {
        return storedLogs.toString();
    }

    private static final class DefaultFormatter extends Formatter {

        static final DefaultFormatter INSTANCE = new DefaultFormatter();
        private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            String date = format.format(new Date(record.getMillis()));
            String log = String.format("[%s] [%s.%s/%s] %s%n",
                    date, record.getSourceClassName(), record.getSourceMethodName(),
                    record.getLevel().getName(), record.getMessage()
            );
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            if (record.getThrown() != null)
                try (PrintWriter writer = new PrintWriter(builder)) {
                    record.getThrown().printStackTrace(writer);
                }
            return log + builder.toString();
        }

    }
}
