package org.bardframework.filestore.file.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bardframework.filestore.file.FileInfo;

/**
 * @author v.zafari@chmail.ir
 */
public class CacheFile {

    private FileInfo file;
    @JsonDeserialize(using = FetchFileFromCacheDeserializer.class)
    private String fileId;

    public CacheFile() {
    }

    public FileInfo getFile() {
        return file;
    }

    public void setFile(FileInfo file) {
        this.file = file;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public byte[] getBytes() {
        return null == file ? null : file.getBytes();
    }
}