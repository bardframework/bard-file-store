file-store-redis
================

[![Maven Central](https://img.shields.io/badge/maven--central-5.3.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/file-store-redis/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Redis storage with a per-entry TTL. Shared across nodes, expiring without a cleanup job — the usual production choice.

Part of [**Bard File Store**](../README.md) · [Bard Framework](https://github.com/bardframework)

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-redis</artifactId>
    <version>5.3.3</version>
</dependency>
```

```java
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder(RedisTemplate<String, FileInfo> template) {
    return new RedisFileHolder(template, 30, TimeUnit.MINUTES) { };
}
```

`RedisFileHolder` is abstract; the anonymous subclass is enough unless you need to customise key
construction.

## Notes

* **The TTL is the abandonment policy.** A user who uploads a photo and closes the tab costs you
  nothing after it expires — no scheduled cleanup, no orphan sweep.
* **Size it for the window, not the corpus.** Only in-flight uploads live here. A 30-minute TTL and a
  few hundred concurrent uploads is a small amount of memory; holding finished files here is not what
  this is for — move them to permanent storage in your `postSave` hook.
* **Redis has a value size limit** (512 MB, and far lower in practice before it hurts). For large
  files prefer [`file-store-fs`](../file-store-fs).

## License

Apache License 2.0.
