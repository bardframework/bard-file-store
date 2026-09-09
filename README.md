Bard File Store
===============

[![Maven Central](https://img.shields.io/badge/maven--central-5.6.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Short-lived, per-user file storage: hold an uploaded file from the moment it is uploaded until the
moment the form that references it is submitted.

Uploads and form submissions are separate requests. The user picks an avatar, the browser uploads it
immediately, and only later does the user press *Save* — or abandon the page entirely. Something has
to hold those bytes in between, key them to the uploading user, and let them expire. That is all this
library does.

`groupId` `org.bardframework.filestore`, version **5.6.3** (managed by [
`bard-bom`](https://github.com/bardframework/bard-bom)).

## Artifacts

| Artifact | Backend | Use when |
| --- | --- | --- |
| `file-store-api` | — | Always. Contracts (`FileInfo`, `UserFileHolder`) plus the upload validators. |
| `file-store-in-memory` | heap, with age-based eviction | Single node, development, tests. |
| `file-store-redis` | Redis, TTL per entry | Multiple nodes. The usual production choice. |
| `file-store-fs` | local/mounted filesystem | Large files you do not want in memory or Redis. |
| `file-store-db` | any table via your own DAO | An existing database is already the system of record. |

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-redis</artifactId>
</dependency>
```

## The contract

```java
public interface UserFileHolder<F extends FileInfo, U> {
    String  save(F file, U user);      // returns an opaque key
    F       get(String key, U user);
    boolean remove(String key, U user);
}
```

`FileInfo` carries `name`, `originalName`, `contentType`, `size`, `bytes` and `createTime`;
`FileInfoImpl` is the default implementation.

Two properties matter:

* **The key is a random UUID**, generated server-side by `UserFileHolderAbstract.save`. It is not
  derived from the filename, so it cannot be guessed or traversed.
* **Every operation takes the user.** `get(key, user)` for a key belonging to someone else returns
  nothing — one user cannot read another's pending upload by guessing a key.

## Choosing an implementation

Each backend is an abstract class; extend it to bind your own user type.

```java
// Redis — key expires by itself
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder(RedisTemplate<String, FileInfo> template) {
    return new RedisFileHolder(template, 30, TimeUnit.MINUTES) { };
}

// In memory — entries older than the given age are evicted
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder() {
    return new FileHolderInMemory<AppUser>(30, ChronoUnit.MINUTES) { };
}

// Filesystem — files land under basePath
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder() {
    return new FileHolderFs<FileInfo, AppUser>("/var/lib/app/uploads") { };
}
```

`FileHolderDb` leaves persistence to you: implement `onSave` / `onGet` / `onRemove` against your own
table.

## Referencing a held file from a DTO

`CacheFile` plus `FetchFileFromCacheDeserializer` let a DTO field carry a *key* on the wire and
arrive as *the file itself*:

```java
public class ProfileDto {
    private CacheFile avatar;   // client sends {"avatar": {"fileId": "9f3c…"}}
}
```

During deserialisation the id is looked up in the file holder and `avatar.getFile()` /
`avatar.getBytes()` are populated. Your service never touches the store directly.

## Upload validators

Built on [`bard-validator`](https://github.com/bardframework/bard-validator), so they are declared
next to your other rules rather than written into the controller:

| Validator | Checks |
| --- | --- |
| `FileSizeValidator(min, max)` | Byte size within bounds. |
| `FileContentTypeValidator(Set<String> validContentTypes)` | Content type against an allow-list. |
| `PictureAspectRatioValidator(double aspectRatio, double tolerance)` | Image aspect ratio — e.g. `1.0` for a square avatar, `1.777` for 16:9 — within a tolerance. |

```xml
<entry key="avatar">
    <list>
        <bean class="org.bardframework.filestore.validator.FileSizeValidator"
              c:minSize="1024" c:maxSize="2097152"/>
        <bean class="org.bardframework.filestore.validator.FileContentTypeValidator">
            <constructor-arg><util:list><value>image/png</value><value>image/jpeg</value></util:list></constructor-arg>
        </bean>
        <bean class="org.bardframework.filestore.validator.PictureAspectRatioValidator"
              c:aspectRatio="1.0" c:tolerance="0.02"/>
    </list>
</entry>
```

> Validate content type from the bytes, not from the client-supplied header — a browser will happily
> claim `image/png` for anything.

## Where it is used

`FileUploadFieldTemplate` and `ImageUploadFieldTemplate` in
[`bard-form`](https://github.com/bardframework/bard-form-parent) upload to a file store and put the
returned key into the form data, so a file field behaves like any other field in a form or a flow.

## License

Apache License 2.0.
