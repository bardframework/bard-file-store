package org.bardframework.filestore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolderAbstract;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by v.zafari on 1/25/2016.
 * <p>
 * thread-safe
 */
public abstract class FileHolderFs<F extends FileInfo, U> extends UserFileHolderAbstract<F, U> {

    protected final String basePath;
    @Autowired
    protected ObjectMapper objectMapper;

    public FileHolderFs(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void onSave(String key, F data, U user) {
        String content;
        try {
            content = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("error serializing file content to json string: " + data, e);
        }
        Path filePath = this.getFilePath(key, user);
        try {
            FileUtils.writeStringToFile(filePath.toFile(), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("error writing value to file: " + filePath, e);
        }
    }

    @Override
    public F onGet(String key, U user) {
        Path filePath = this.getFilePath(key, user);
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

    protected Path getFilePath(String key, U user) {
        return Paths.get(basePath, user.toString(), key);
    }

    @Override
    public boolean onRemove(String key, U user) {
        Path filePath = this.getFilePath(key, user);
        return FileUtils.deleteQuietly(filePath.toFile());
    }

    protected abstract Class<? extends F> getFileInfoClass();
}