package me.armar.plugins.autorank.logger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogFile {
    DateTimeFormatter timeFormat;
    private final String fileName;
    private PrintWriter pw;
    private boolean fileReady;

    public LogFile(String fileName) {
        this.timeFormat = DateTimeFormatter.ISO_LOCAL_TIME;
        this.fileReady = false;
        this.fileName = fileName;
    }

    public boolean isFileReady() {
        return this.fileReady;
    }

    public void loadFile() {
        try {
            File file = new File(this.fileName);
            FileWriter fw;
            if (file.exists()) {
                fw = new FileWriter(file, true);
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                fw = new FileWriter(file);
            }

            BufferedWriter bw = new BufferedWriter(fw);
            this.pw = new PrintWriter(bw);
            this.fileReady = true;
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void writeToFile(String message) {
        if (this.isFileReady() && message != null) {
            PrintWriter var10000 = this.pw;
            String var10001 = LocalTime.now().format(this.timeFormat);
            var10000.println("[" + var10001 + "]: " + message);
            this.pw.flush();
        }

    }
}
