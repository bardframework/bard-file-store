package org.bardframework.filestore.db;

import org.bardframework.filestore.holder.UserFileHolderAbstract;

import java.util.concurrent.TimeUnit;

/**
 * Created by v.zafari on 1/25/2016.
 *
 * @thread-safe
 */
public abstract class FileHolderDb<F> extends UserFileHolderAbstract<F> {

    public FileHolderDb(long fileAge, TimeUnit ageUnit) {
        super(fileAge, ageUnit);
    }

    @Override
    public void onSave(String key, F data, String userId) {

    }

    @Override
    public F onGet(String key, String userId) {
        return null;
    }

    @Override
    public boolean onRemove(String key, String userId) {
        return false;
    }
}