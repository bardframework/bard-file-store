package org.bardframework.filestore.holder;

import org.bardframework.commons.utils.AssertionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 */
public abstract class UserFileHolderAbstract<F> implements UserFileHolder<F> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    protected final long fileAge;
    protected final TimeUnit ageUnit;

    protected UserFileHolderAbstract(long fileAge, TimeUnit ageUnit) {
        this.fileAge = fileAge;
        this.ageUnit = ageUnit;
    }

    public String save(F file, String userId) {
        AssertionUtils.notNull(userId, "null userId not acceptable");
        AssertionUtils.notNull(file, "null data not acceptable");
        String identifier = UUID.randomUUID().toString();
        this.onSave(identifier, file, userId);
        return identifier;
    }

    public abstract void onSave(String key, F file, String userId);

    @Override
    public F get(String key, String userId) {
        AssertionUtils.hasLength(key, "null or empty key not acceptable");
        AssertionUtils.notNull(userId, "null userId not acceptable");
        return this.onGet(key, userId);
    }

    public abstract F onGet(String key, String userId);

    public boolean remove(String key, String userId) {
        AssertionUtils.hasLength(key, "null or empty key not acceptable");
        AssertionUtils.notNull(userId, "null userId not acceptable");
        return this.onRemove(key, userId);
    }

    public abstract boolean onRemove(String key, String userId);

}
