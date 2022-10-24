package me.armar.plugins.autorank.logger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogFile {
    DateTimeFormatter timeFormat;
    private String fileName;
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter pw;
    private boolean fileReady;

    public LogFile(String fileName) {
        this.timeFormat = DateTimeFormatter.ISO_LOCAL_TIME;
        this.fileName = "log-file";
        this.fileReady = false;
        this.fileName = fileName;
    }

    public boolean isFileReady() {
        return this.fileReady;
    }

    public void loadFile() {
        try {
            File file = new File(this.fileName);
            if (file.exists()) {
                this.fw = new FileWriter(file, true);
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                this.fw = new FileWriter(file);
            }

            this.bw = new BufferedWriter(this.fw);
            this.pw = new PrintWriter(this.bw);
            this.fileReady = true;
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void writeToFile(String message) {
        if (this.isFileReady()) {
            if (message != null) {
                this.pw.println("[" + LocalTime.now().format(this.timeFormat) + "]: " + message);
                this.pw.flush();
            }
        }
    }
}
