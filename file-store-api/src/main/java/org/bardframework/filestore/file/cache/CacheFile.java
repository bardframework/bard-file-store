package org.bardframework.filestore.file.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.bardframework.filestore.file.FileInfo;

/**
 * @author v.zafari@chmail.ir
 */
@Getter
@Setter
public class CacheFile {

    private FileInfo file;
    @JsonDeserialize(using = FetchFileFromCacheDeserializer.class)
    private String fileId;

    public CacheFile() {
    }

    public byte[] getBytes() {
        return null == file ? null : file.getBytes();
    }
}