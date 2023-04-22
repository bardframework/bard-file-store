package org.bardframework.filestore.file.cache;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolder;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by Vahid Zafari on 10/28/2016.
 */
@Slf4j
public class FetchFileFromCacheDeserializer<U> extends JsonDeserializer<Object> {

    private final UserFileHolder<FileInfo, ?> fileHolder;

    public FetchFileFromCacheDeserializer(UserFileHolder<FileInfo, ?> fileHolder) {
        this.fileHolder = fileHolder;
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) {
        try {
            ((CacheFile) parser.getCurrentValue()).setFile(fileHolder.get(parser.getValueAsString(), null));
            return parser.getValueAsString();
        } catch (Exception e) {
            log.error("error fetching data from cache and set to object, annotated field for deserialize with '{}' must be within '{}' class.", getClass(), CacheFile.class);
            throw new IllegalStateException("error fetching data from cache and set to object", e);
        }
    }
}