package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PlayTimeStorageManager {
    private final List<PlayTimeStorageProvider> activeStorageProviders = new ArrayList<>();
    private PlayTimeStorageProvider primaryStorageProvider = null;
    private final Autorank plugin;

    public PlayTimeStorageManager(Autorank instance) {
        this.plugin = instance;
    }

    public PlayTimeStorageProvider getPrimaryStorageProvider() {
        return this.primaryStorageProvider;
    }

    public void setPrimaryStorageProvider(PlayTimeStorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        } else {
            this.primaryStorageProvider = storageProvider;
        }
    }

    public List<String> getActiveStorageProviders() {
        List<String> storageProviders = new ArrayList<>();

        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            storageProviders.add(storageProvider.getName());
        }

        return storageProviders;
    }

    public PlayTimeStorageProvider getActiveStorageProvider(String providerName) {
        return this.activeStorageProviders.stream().filter((provider) -> provider.getName().equalsIgnoreCase(providerName)).findFirst().orElse(null);
    }

    public void registerStorageProvider(PlayTimeStorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        } else {
            this.activeStorageProviders.add(storageProvider);
            if (this.getPrimaryStorageProvider() == null) {
                this.setPrimaryStorageProvider(storageProvider);
            }

            String var10001 = storageProvider.getName();
            this.plugin.debugMessage("Registered new storage provider: " + var10001 + " (type: " + storageProvider.getStorageType() + ")");
        }
    }

    public void saveAllStorageProviders() {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            storageProvider.saveData();
        }

    }

    public void doCalendarCheck() {
        this.plugin.debugMessage("Performing a calendar check!");
        this.checkDataIsUpToDate();
    }

    public void setPlayerTime(TimeType timeType, UUID uuid, int value) {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            storageProvider.setPlayerTime(timeType, uuid, value);
        }

    }

    public void setPlayerTime(UUID uuid, int value) {
        TimeType[] var3 = TimeType.values();

        for(TimeType timeType : var3) {
            this.setPlayerTime(timeType, uuid, value);
        }

    }

    public void setPlayerTime(PlayTimeStorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach((storageProviderName) -> {
            PlayTimeStorageProvider storageProvider = this.getActiveStorageProvider(storageProviderName);
            if (storageProvider != null && storageProvider.getStorageType() == storageType) {
                storageProvider.setPlayerTime(timeType, uuid, value);
            }

        });
    }

    public void addPlayerTime(TimeType timeType, UUID uuid, int value) {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            storageProvider.addPlayerTime(timeType, uuid, value);
        }

    }

    public void addPlayerTime(UUID uuid, int value) {
        TimeType[] var3 = TimeType.values();

        for(TimeType timeType : var3) {
            this.addPlayerTime(timeType, uuid, value);
        }

    }

    public void addPlayerTime(PlayTimeStorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach((storageProviderName) -> {
            PlayTimeStorageProvider storageProvider = this.getActiveStorageProvider(storageProviderName);
            if (storageProvider != null && storageProvider.getStorageType() == storageType) {
                storageProvider.addPlayerTime(timeType, uuid, value);
            }

        });
    }

    public boolean isStorageTypeActive(PlayTimeStorageProvider.StorageType storageType) {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            if (storageProvider.getStorageType() == storageType) {
                return true;
            }
        }

        return false;
    }

    public PlayTimeStorageProvider getStorageProvider(PlayTimeStorageProvider.StorageType storageType) {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            if (storageProvider.getStorageType() == storageType) {
                return storageProvider;
            }
        }

        return null;
    }

    public void backupStorageProviders() {
        for(PlayTimeStorageProvider storageProvider : this.activeStorageProviders) {
            if (storageProvider.canBackupData()) {
                boolean var3 = storageProvider.backupData();
            }
        }

    }

    public void checkDataIsUpToDate() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            LocalDate today = LocalDate.now();
            this.plugin.debugMessage("Running check to see if data files are still up to date.");
            TimeType[] var2 = TimeType.values();

            for(TimeType type : var2) {
                if (this.isDataFileOutdated(type)) {
                    this.activeStorageProviders.forEach((provider) -> provider.resetData(type));
                    int value = 0;
                    String broadcastMessage = "";
                    if (type == TimeType.DAILY_TIME) {
                        value = today.getDayOfWeek().getValue();
                        broadcastMessage = Lang.RESET_DAILY_TIME.getConfigValue();
                    } else if (type == TimeType.WEEKLY_TIME) {
                        value = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        broadcastMessage = Lang.RESET_WEEKLY_TIME.getConfigValue();
                    } else if (type == TimeType.MONTHLY_TIME) {
                        value = today.getMonthValue();
                        broadcastMessage = Lang.RESET_MONTHLY_TIME.getConfigValue();
                    }

                    if (this.plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                        AutorankTools.allDeserialize(broadcastMessage);
                    }

                    this.plugin.getInternalPropertiesConfig().setTrackedTimeType(type, value);
                    this.plugin.getInternalPropertiesConfig().setLeaderboardLastUpdateTime(type, 0L);
                    this.plugin.getLeaderboardManager().updateLeaderboard(type);
                }
            }

        });
    }

    public boolean isDataFileOutdated(TimeType timeType) {
        LocalDate today = LocalDate.now();
        if (timeType == TimeType.TOTAL_TIME) {
            return false;
        } else {
            int trackedTimeType = this.plugin.getInternalPropertiesConfig().getTrackedTimeType(timeType);
            if (timeType == TimeType.DAILY_TIME) {
                return trackedTimeType != today.getDayOfWeek().getValue();
            } else if (timeType == TimeType.WEEKLY_TIME) {
                return trackedTimeType != today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            } else if (timeType == TimeType.MONTHLY_TIME) {
                return trackedTimeType != today.getMonthValue();
            } else {
                return false;
            }
        }
    }
}
