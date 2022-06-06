package org.bardframework.filestore;

import org.bardframework.commons.utils.DateTimeUtils;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolderAbstract;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 * <p>
 * thread-safe
 */
public abstract class FileHolderInMemory<U> extends UserFileHolderAbstract<FileInfo, U> {

    protected final ConcurrentMap<U, Map<String, FileInfo>> dataHolder;
    protected final long fileAge;
    protected final TimeUnit ageUnit;

    public FileHolderInMemory(long fileAge, TimeUnit ageUnit) {
        this.fileAge = fileAge;
        this.ageUnit = ageUnit;
        this.dataHolder = new ConcurrentHashMap<>();
    }

    @Override
    public void onSave(String key, FileInfo file, U user) {
        dataHolder.putIfAbsent(user, new ConcurrentHashMap<>());
        dataHolder.get(user).put(key, file);
    }

    @Override
    public FileInfo onGet(String key, U user) {
        if (!dataHolder.containsKey(user)) {
            return null;
        }
        return dataHolder.get(user).get(key);
    }

    @Override
    public boolean onRemove(String key, U user) {
        if (!dataHolder.containsKey(user)) {
            throw new IllegalStateException("user: " + user + " not found.");
        }
        if (!dataHolder.get(user).containsKey(key)) {
            throw new IllegalStateException("file with id: " + key + " for user: " + user + " not found.");
        }
        return null != dataHolder.get(user).remove(key);
    }

    @Scheduled(cron = "${fileHolder.inMemory.cleaner.cron:0 */1 * * * *}")
    protected void clean() {
        LOGGER.info("running file cleaner task");
        if (dataHolder.isEmpty()) {
            LOGGER.info("dataHolder has no entry.");
            return;
        }
        dataHolder.keySet().forEach(userId -> {
            Map<String, FileInfo> userFileMap = dataHolder.get(userId);
            if (null == userFileMap) {
                dataHolder.remove(userId);
            } else {
                userFileMap.keySet().forEach(key -> {
                    FileInfo fileInfo = userFileMap.get(key);
                    if (null == fileInfo || fileInfo.getCreateTime().until(LocalDateTime.now(), DateTimeUtils.toChronoUnit(ageUnit)) > fileAge) {
                        LOGGER.info("cleaning aged file {}", fileInfo);
                        userFileMap.remove(key);
                    }
                });
                if (userFileMap.isEmpty()) {
                    dataHolder.remove(userId);
                }
            }
        });
    }
}