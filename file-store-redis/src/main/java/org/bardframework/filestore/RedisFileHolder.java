package org.bardframework.filestore;

import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolderAbstract;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 * <p>
 * thread-safe
 */
public abstract class RedisFileHolder extends UserFileHolderAbstract<FileInfo, Object> {

    protected final RedisTemplate<String, FileInfo> redisTemplate;
    protected final long fileAge;
    protected final TimeUnit ageUnit;

    public RedisFileHolder(RedisTemplate<String, FileInfo> redisTemplate, long fileAge, TimeUnit ageUnit) {
        this.fileAge = fileAge;
        this.ageUnit = ageUnit;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onSave(String key, FileInfo data, Object user) {
        String fileKey = this.getFileKey(key, user);
        redisTemplate.opsForValue().set(fileKey, data, fileAge, ageUnit);
        redisTemplate.persist(fileKey);
    }

    @Override
    public FileInfo onGet(String key, Object user) {
        return redisTemplate.opsForValue().get(this.getFileKey(key, user));
    }

    @Override
    public boolean onRemove(String key, Object user) {
        return redisTemplate.opsForValue().getOperations().delete(this.getFileKey(key, user));
    }

    protected String getFileKey(String key, Object user) {
        return user + "\\" + key;
    }
}