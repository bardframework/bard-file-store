package org.bardframework.filestore.holder;

/**
 * Created by v.zafari on 1/25/2016.
 */
public interface UserFileHolder<F> {

    /**
     * @param file   the data that must be hold
     * @param userId
     * @return key of held data
     * @throws IllegalArgumentException if data be null
     */
    String save(F file, String userId);

    /**
     * retrieve data with given #key belong to #user
     *
     * @param key    key of data that must be retrieved
     * @param userId
     * @return data with given identifier
     * @throws IllegalArgumentException if user or key be null
     * @throws NotExistException        if no data exist with given key
     */
    F get(String key, String userId);

    /**
     * remove data with given #key
     *
     * @param key    key of data that must be retrieved
     * @param userId
     * @return <code>true</code> if data with given key removed, <code>false</code> otherwise
     * @throws IllegalArgumentException if user or key be null
     * @throws NotExistException        if no data exist with given key
     */
    boolean remove(String key, String userId);
}
