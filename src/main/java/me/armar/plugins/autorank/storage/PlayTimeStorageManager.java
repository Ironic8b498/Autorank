package me.armar.plugins.autorank.storage;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.language.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

public class PlayTimeStorageManager {
    private final List<PlayTimeStorageProvider> activeStorageProviders = new ArrayList();
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
        List<String> storageProviders = new ArrayList();
        Iterator var2 = this.activeStorageProviders.iterator();

        while(var2.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var2.next();
            storageProviders.add(storageProvider.getName());
        }

        return storageProviders;
    }

    public PlayTimeStorageProvider getActiveStorageProvider(String providerName) {
        return this.activeStorageProviders.stream().filter((provider) -> {
            return provider.getName().equalsIgnoreCase(providerName);
        }).findFirst().orElseGet(() -> {
            return null;
        });
    }

    public void registerStorageProvider(PlayTimeStorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        } else {
            this.activeStorageProviders.add(storageProvider);
            if (this.getPrimaryStorageProvider() == null) {
                this.setPrimaryStorageProvider(storageProvider);
            }

            this.plugin.debugMessage("Registered new storage provider: " + storageProvider.getName() + " (type: " + storageProvider.getStorageType() + ")");
        }
    }

    public void deRegisterStorageProvider(PlayTimeStorageProvider storageProvider) throws IllegalArgumentException {
        if (storageProvider == null) {
            throw new IllegalArgumentException("StorageProvider cannot be null.");
        } else {
            this.activeStorageProviders.remove(storageProvider);
        }
    }

    public void saveAllStorageProviders() {
        Iterator var1 = this.activeStorageProviders.iterator();

        while(var1.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var1.next();
            storageProvider.saveData();
        }

    }

    public void doCalendarCheck() {
        this.plugin.debugMessage("Performing a calendar check!");
        this.checkDataIsUpToDate();
    }

    public void setPlayerTime(TimeType timeType, UUID uuid, int value) {
        Iterator var4 = this.activeStorageProviders.iterator();

        while(var4.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var4.next();
            storageProvider.setPlayerTime(timeType, uuid, value);
        }

    }

    public void setPlayerTime(UUID uuid, int value) {
        TimeType[] var3 = TimeType.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TimeType timeType = var3[var5];
            this.setPlayerTime(timeType, uuid, value);
        }

    }

    public void setPlayerTime(PlayTimeStorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach((storageProviderName) -> {
            PlayTimeStorageProvider storageProvider = this.getActiveStorageProvider(storageProviderName);
            if (storageProvider != null) {
                if (storageProvider.getStorageType() == storageType) {
                    storageProvider.setPlayerTime(timeType, uuid, value);
                }
            }
        });
    }

    public void addPlayerTime(TimeType timeType, UUID uuid, int value) {
        Iterator var4 = this.activeStorageProviders.iterator();

        while(var4.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var4.next();
            storageProvider.addPlayerTime(timeType, uuid, value);
        }

    }

    public void addPlayerTime(UUID uuid, int value) {
        TimeType[] var3 = TimeType.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TimeType timeType = var3[var5];
            this.addPlayerTime(timeType, uuid, value);
        }

    }

    public void addPlayerTime(PlayTimeStorageProvider.StorageType storageType, TimeType timeType, UUID uuid, int value) {
        this.getActiveStorageProviders().forEach((storageProviderName) -> {
            PlayTimeStorageProvider storageProvider = this.getActiveStorageProvider(storageProviderName);
            if (storageProvider != null) {
                if (storageProvider.getStorageType() == storageType) {
                    storageProvider.addPlayerTime(timeType, uuid, value);
                }
            }
        });
    }

    public boolean isStorageTypeActive(PlayTimeStorageProvider.StorageType storageType) {
        Iterator var2 = this.activeStorageProviders.iterator();

        PlayTimeStorageProvider storageProvider;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            storageProvider = (PlayTimeStorageProvider)var2.next();
        } while(storageProvider.getStorageType() != storageType);

        return true;
    }

    public PlayTimeStorageProvider getStorageProvider(PlayTimeStorageProvider.StorageType storageType) {
        Iterator var2 = this.activeStorageProviders.iterator();

        PlayTimeStorageProvider storageProvider;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            storageProvider = (PlayTimeStorageProvider)var2.next();
        } while(storageProvider.getStorageType() != storageType);

        return storageProvider;
    }

    public void importDataForStorageProviders() {
        Iterator var1 = this.activeStorageProviders.iterator();

        while(var1.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var1.next();
            if (storageProvider.canImportData()) {
                storageProvider.importData();
            }
        }

    }

    public boolean backupStorageProviders() {
        boolean successfulBackup = true;
        Iterator var2 = this.activeStorageProviders.iterator();

        while(var2.hasNext()) {
            PlayTimeStorageProvider storageProvider = (PlayTimeStorageProvider)var2.next();
            if (storageProvider.canBackupData()) {
                boolean result = storageProvider.backupData();
                if (!result) {
                    successfulBackup = false;
                }
            }
        }

        return successfulBackup;
    }

    public void checkDataIsUpToDate() {
        var mm = MiniMessage.miniMessage();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            LocalDate today = LocalDate.now();
            this.plugin.debugMessage("Running check to see if data files are still up to date.");
            TimeType[] var2 = TimeType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                TimeType type = var2[var4];
                if (this.isDataFileOutdated(type)) {
                    this.activeStorageProviders.forEach((provider) -> {
                        provider.resetData(type);
                    });
                    int value = 0;
                    String broadcastMessage = "";
                    if (type == TimeType.DAILY_TIME) {
                        value = today.getDayOfWeek().getValue();
                        broadcastMessage = Lang.RESET_DAILY_TIME.getConfigValue();
//                        Component you_should_specify = mm.deserialize(Lang.YOU_SHOULD_SPECIFY.getConfigValue());
//                        plugin.adventure().player((Player) sender).sendMessage(you_should_specify);
                    } else if (type == TimeType.WEEKLY_TIME) {
                        value = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        broadcastMessage = Lang.RESET_WEEKLY_TIME.getConfigValue();
                    } else if (type == TimeType.MONTHLY_TIME) {
                        value = today.getMonthValue();
                        broadcastMessage = Lang.RESET_MONTHLY_TIME.getConfigValue();
                    }

                    if (this.plugin.getSettingsConfig().shouldBroadcastDataReset()) {
                        this.plugin.getServer().broadcastMessage(broadcastMessage);
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
