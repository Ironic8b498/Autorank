package me.armar.plugins.autorank.logger;

import me.armar.plugins.autorank.Autorank;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoggerManager {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Autorank autorank;
    private LogFile logFile;

    public LoggerManager(Autorank autorank) {
        this.autorank = autorank;
        this.loadLogFile();
    }

    public void logMessage(String message) {
        if (message != null) {
            if (this.autorank.getSettingsConfig().isLoggingEnabled()) {
                LogFile currentLogFile = this.getCurrentLogFile();
                if (currentLogFile == null) {
                    this.autorank.getLogger().severe("Autorank could not start its logger. Autorank will not log!");
                } else if (!currentLogFile.isFileReady()) {
                    this.autorank.getLogger().severe("Autorank create a log file but can't load it. Autorank will not log!");
                } else {
                    currentLogFile.writeToFile(message);
                }
            }
        }
    }

    private LogFile getCurrentLogFile() {
        if (this.logFile == null) {
            this.generateNewLogFile();
        }

        return this.logFile;
    }

    private void generateNewLogFile() {
        LocalDate logFileDate = LocalDate.now();
        this.logFile = new LogFile(this.autorank.getDataFolder().getAbsolutePath() + File.separator + "logging" + File.separator + "log-" + dateFormat.format(logFileDate) + ".txt");
        this.autorank.getLogger().info("Generated new log file: log-" + dateFormat.format(logFileDate) + ".txt");
        this.logFile.loadFile();
    }

    public void loadLogFile() {
        this.generateNewLogFile();
    }
}
