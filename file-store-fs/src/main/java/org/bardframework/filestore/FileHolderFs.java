package org.bardframework.filestore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.bardframework.filestore.holder.UserFileHolderAbstract;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 * <p>
 * thread-safe
 */
public abstract class FileHolderFs<F> extends UserFileHolderAbstract<F> {

    protected final String basePath;
    @Autowired
    protected ObjectMapper objectMapper;

    public FileHolderFs(String basePath, long fileAge, TimeUnit ageUnit) {
        super(fileAge, ageUnit);
        this.basePath = basePath;
    }

    @Override
    public void onSave(String key, F data, String userId) {
        String content;
        try {
            content = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("error serializing file content to json string: " + data, e);
        }
        Path filePath = this.getFilePath(key, userId);
        try {
            FileUtils.writeStringToFile(filePath.toFile(), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("error writing value to file: " + filePath, e);
        }
    }

    @Override
    public F onGet(String key, String userId) {
        Path filePath = this.getFilePath(key, userId);
        String content;
        try {
            content = FileUtils.readFileToString(filePath.toFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("error writing value to file: " + filePath, e);
        }
        try {
            return objectMapper.readValue(content, this.getFileInfoClass());
        } catch (IOException e) {
            throw new IllegalStateException("error deserializing file content to : " + this.getFileInfoClass(), e);
        }
    }

    protected Path getFilePath(String key, String userId) {
        return Paths.get(basePath, userId, key);
    }

    @Override
    public boolean onRemove(String key, String userId) {
        Path filePath = this.getFilePath(key, userId);
        return FileUtils.deleteQuietly(filePath.toFile());
    }

    protected abstract Class<? extends F> getFileInfoClass();
}