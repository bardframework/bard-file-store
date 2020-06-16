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
 *
 * thread-safe
 */
public abstract class FileHolderInMemory<U> extends UserFileHolderAbstract<FileInfo> {

    protected final ConcurrentMap<String, Map<String, FileInfo>> dataHolder;

    public FileHolderInMemory(long fileAge, TimeUnit ageUnit) {
        super(fileAge, ageUnit);
        this.dataHolder = new ConcurrentHashMap<>();
    }

    @Override
    public void onSave(String key, FileInfo file, String userId) {
        dataHolder.putIfAbsent(userId, new ConcurrentHashMap<>());
        dataHolder.get(userId).put(key, file);
    }

    @Override
    public FileInfo onGet(String key, String userId) {
        if (!dataHolder.containsKey(userId)) {
            return null;
        }
        return dataHolder.get(userId).get(key);
    }

    @Override
    public boolean onRemove(String key, String userId) {
        if (!dataHolder.containsKey(userId)) {
            throw new IllegalStateException("user: " + userId + " not found.");
        }
        if (!dataHolder.get(userId).containsKey(key)) {
            throw new IllegalStateException("file with id: " + key + " for user: " + userId + " not found.");
        }
        return null != dataHolder.get(userId).remove(key);
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