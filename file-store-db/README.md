file-store-db
=============

[![Maven Central](https://img.shields.io/badge/maven--central-5.6.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/file-store-db/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Database storage. Extend it and supply the persistence; useful when the database is already the system of record and you want one backup story.

Part of [**Bard File Store**](../README.md) · [Bard Framework](https://github.com/bardframework)

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-db</artifactId>
    <version>5.6.3</version>
</dependency>
```

`FileHolderDb<F extends FileInfo, U>` deliberately implements nothing — it exists so a database-backed
holder fits the same contract as the others. Supply the three operations against your own table or
DAO:

```java
@Repository
public class AppFileHolderDb extends FileHolderDb<FileInfo, AppUser> {

    private final FileDao dao;
    public AppFileHolderDb(FileDao dao) { this.dao = dao; }

    @Override public void onSave(String key, FileInfo file, AppUser user) {
        dao.insert(key, user.getId(), file.getOriginalName(), file.getContentType(), file.getBytes());
    }

    @Override public FileInfo onGet(String key, AppUser user) {
        return dao.find(key, user.getId());
    }

    @Override public boolean onRemove(String key, AppUser user) {
        return dao.delete(key, user.getId()) > 0;
    }
}
```

Scope every query by the user — the contract expects that a key belonging to someone else yields
nothing.

## When this is the right choice

* Uploads must be inside the same transaction and the same backup as the rest of the data.
* Regulatory requirements make an extra data store expensive to justify.
* Files are small and volume is modest.

## When it is not

Large BLOBs make backups slow and buffer pools cold. If the files are big or numerous, prefer
[`file-store-fs`](../file-store-fs) or object storage and keep only a reference in the database.

Remember these are *temporary* holdings; add a scheduled delete of rows older than your upload
window.

## License

Apache License 2.0.
