package org.bardframework.filestore.file.cache;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by Vahid Zafari on 10/28/2016.
 */
public class FetchFileFromCacheDeserializer<U> extends JsonDeserializer<Object> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FetchFileFromCacheDeserializer.class);

    private final UserFileHolder<FileInfo> fileHolder;

    public FetchFileFromCacheDeserializer(UserFileHolder<FileInfo> fileHolder) {
        this.fileHolder = fileHolder;
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) {
        try {
            ((CacheFileDto) parser.getCurrentValue()).setFile(fileHolder.get(parser.getValueAsString(), null));
            return parser.getValueAsString();
        } catch (Exception e) {
            LOGGER.error("error fetching data from cache and set to object, annotated field for deserialize with '{}' must be within '{}' class.", getClass(), CacheFileDto.class);
            throw new IllegalStateException("error fetching data from cache and set to object", e);
        }
    }
}