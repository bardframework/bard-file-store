package org.bardframework.filestore.file;

import java.time.LocalDateTime;

/**
 * A representation of a file.
 *
 * <p>The file contents are either stored in memory or temporarily on disk.
 * In either case, the user is responsible for copying file contents to a
 * session-level or persistent store as and if desired. The temporary storages
 * will be cleared at the end of request processing.
 * <p>
 * Created by Vahid Zafari on 10/28/2016.
 */
public interface FileInfo {

    /**
     * Return the name of the file.
     *
     * @return the name of the parameter (never {@code null} or empty)
     */
    String getName();

    /**
     * Return the original filename in the client's filesystem.
     * <p>This may contain path information depending on the browser used,
     * but it typically will not with any other than Opera.
     *
     * @return the original filename, or the empty String
     */
    String getOriginalName();

    /**
     * Return the content type of the file.
     *
     * @return the content type, or {@code null} if not defined
     */
    String getContentType();

    /**
     * @return true file is empty
     */
    boolean isEmpty();

    /**
     * Return the size of the file in bytes.
     *
     * @return the size of the file, or 0 if empty
     */
    long getSize();

    /**
     * Return the contents of the file as an array of bytes.
     *
     * @return the contents of the file as bytes, or an empty byte array if empty
     */
    byte[] getBytes();

    LocalDateTime getCreateTime();
}
