file-store-fs
=============

[![Maven Central](https://img.shields.io/badge/maven--central-6.1.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/file-store-fs/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Filesystem storage. For files too large to sit comfortably in memory or Redis.

Part of [**Bard File Store**](../README.md) · [Bard Framework](https://github.com/bardframework)

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-fs</artifactId>
    <version>6.1.3</version>
</dependency>
```

```java
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder() {
    return new FileHolderFs<FileInfo, AppUser>("/var/lib/app/uploads") { };
}
```

Files are written under `basePath`, named by the generated UUID key — never by the client-supplied
filename, so a crafted `originalName` cannot escape the directory.

## Operational notes

* **Multiple nodes need shared storage.** A local directory has the same split-brain problem as
  in-memory storage; point `basePath` at NFS, EFS or an equivalent, or use
  [`file-store-redis`](../file-store-redis).
* **There is no automatic expiry.** Unlike the Redis and in-memory backends, nothing removes an
  abandoned upload — schedule a cleanup of files older than your window, using
  `FileInfo.getCreateTime()` or the filesystem timestamp.
* **Ensure the directory exists and is writable** by the application user, and that it is not served
  by your web server.

## License

Apache License 2.0.
