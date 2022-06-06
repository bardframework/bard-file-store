package org.bardframework.filestore.db;

import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.holder.UserFileHolderAbstract;

/**
 * Created by v.zafari on 1/25/2016.
 * <p>
 * thread-safe
 */
public abstract class FileHolderDb<F extends FileInfo, U> extends UserFileHolderAbstract<F, U> {

    @Override
    public void onSave(String key, F data, U user) {
    }

    @Override
    public F onGet(String key, U user) {
        return null;
    }

    @Override
    public boolean onRemove(String key, U user) {
        return false;
    }
}