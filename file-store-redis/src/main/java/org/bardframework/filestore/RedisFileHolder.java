package org.bardframework.filestore;

import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolderAbstract;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 *
 * thread-safe
 */
public abstract class RedisFileHolder extends UserFileHolderAbstract<FileInfo> {

    private final RedisTemplate<String, FileInfo> redisTemplate;

    public RedisFileHolder(RedisTemplate<String, FileInfo> redisTemplate, long fileAge, TimeUnit ageUnit) {
        super(fileAge, ageUnit);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onSave(String key, FileInfo data, String userId) {
        String fileKey = this.getFileKey(key, userId);
        redisTemplate.opsForValue().set(fileKey, data, fileAge, ageUnit);
        redisTemplate.persist(fileKey);
    }

    @Override
    public FileInfo onGet(String key, String userId) {
        return redisTemplate.opsForValue().get(this.getFileKey(key, userId));
    }

    @Override
    public boolean onRemove(String key, String userId) {
        return redisTemplate.opsForValue().getOperations().delete(this.getFileKey(key, userId));
    }

    protected String getFileKey(String key, String userId) {
        return userId + "\\" + key;
    }
}