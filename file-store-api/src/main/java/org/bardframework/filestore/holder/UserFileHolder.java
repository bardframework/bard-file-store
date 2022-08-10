package org.bardframework.filestore.holder;

import org.bardframework.filestore.file.FileInfo;

/**
 * Created by v.zafari on 1/25/2016.
 */
public interface UserFileHolder<F extends FileInfo, U> {

    /**
     * @param file the data that must be hold
     * @return key of held data
     * @throws IllegalArgumentException if data be null
     */
    String save(F file, U user);

    /**
     * retrieve data with given #key belong to #user
     *
     * @param key key of data that must be retrieved
     * @return data with given identifier
     * @throws IllegalArgumentException if user or key be null
     */
    F get(String key, U user);

    /**
     * remove data with given #key
     *
     * @param key key of data that must be retrieved
     * @return <code>true</code> if data with given key removed, <code>false</code> otherwise
     * @throws IllegalArgumentException if user or key be null
     */
    boolean remove(String key, U user);
}
