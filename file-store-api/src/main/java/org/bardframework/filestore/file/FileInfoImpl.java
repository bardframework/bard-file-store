package org.bardframework.filestore.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Vahid Zafari on 6/6/2016.
 */
public class FileInfoImpl implements FileInfo {

    private byte[] bytes;
    private String name;
    private String originalName;
    private String contentType;
    private Enum<?> type;
    private LocalDateTime createTime;

    public FileInfoImpl() {
    }

    public FileInfoImpl(byte[] bytes, String contentType) {
        this(bytes, null, contentType, null);
    }

    public FileInfoImpl(byte[] bytes, String name, String contentType, Enum<?> type) {
        this.bytes = bytes;
        this.name = name;
        this.originalName = name;
        this.contentType = contentType;
        this.type = type;
        this.createTime = LocalDateTime.now();
    }

    public FileInfoImpl(MultipartFile file) throws IOException {
        this.bytes = file.getBytes();
        this.name = file.getName();
        this.originalName = file.getOriginalFilename();
        this.contentType = file.getContentType();
        this.createTime = LocalDateTime.now();
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalName() {
        return originalName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @JsonIgnore
    @Override
    public boolean isEmpty() {
        return null == bytes || bytes.length == 0;
    }

    @JsonIgnore
    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + "'" +
                ", contentType='" + contentType + "'" +
                ", createTime=" + createTime +
                '}';
    }
}
