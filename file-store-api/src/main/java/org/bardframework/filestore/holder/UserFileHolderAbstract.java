package org.bardframework.filestore.holder;

import org.bardframework.commons.utils.AssertionUtils;
import org.bardframework.filestore.file.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by v.zafari on 1/25/2016.
 */
public abstract class UserFileHolderAbstract<F extends FileInfo, U> implements UserFileHolder<F, U> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public String save(F file, U user) {
        AssertionUtils.notNull(user, "null user not acceptable");
        AssertionUtils.notNull(file, "null data not acceptable");
        String identifier = UUID.randomUUID().toString();
        this.onSave(identifier, file, user);
        return identifier;
    }

    public abstract void onSave(String key, F file, U user);

    @Override
    public F get(String key, U user) {
        AssertionUtils.hasText(key, "null or empty key not acceptable");
        AssertionUtils.notNull(user, "null user not acceptable");
        return this.onGet(key, user);
    }

    public abstract F onGet(String key, U user);

    @Override
    public boolean remove(String key, U user) {
        AssertionUtils.hasText(key, "null or empty key not acceptable");
        AssertionUtils.notNull(user, "null user not acceptable");
        return this.onRemove(key, user);
    }

    public abstract boolean onRemove(String key, U user);

}
