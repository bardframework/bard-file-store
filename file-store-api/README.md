file-store-api
==============

[![Maven Central](https://img.shields.io/badge/maven--central-6.1.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/file-store-api/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

The contracts and the upload validators. Every other module in this repository implements what is defined here.

Part of [**Bard File Store**](../README.md) · [Bard Framework](https://github.com/bardframework)

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-api</artifactId>
    <version>5.6.3</version>
</dependency>
```

## UserFileHolder

```java
public interface UserFileHolder<F extends FileInfo, U> {
    String  save(F file, U user);      // → opaque key
    F       get(String key, U user);
    boolean remove(String key, U user);
}
```

`UserFileHolderAbstract` implements the guards and key generation, leaving you three methods:
`onSave`, `onGet`, `onRemove`.

Two properties are structural rather than incidental:

* **The key is a server-generated UUID.** It is not derived from the filename, so it cannot be
  guessed and carries no path to traverse.
* **Every call takes the user.** Reading someone else's pending upload by guessing a key is not
  possible, because the key alone is not enough.

## FileInfo

```java
String        getName();
String        getOriginalName();   // as the client sent it — never trust it for storage
String        getContentType();
boolean       isEmpty();
int           getSize();
byte[]        getBytes();
LocalDateTime getCreateTime();
```

`FileInfoImpl` is the default implementation. `getCreateTime` is what the age-based backends use for
eviction.

## CacheFile

Lets a DTO field carry a *key* on the wire and arrive as *the file*:

```java
public class ProfileDto {
    private CacheFile avatar;      // client sends {"avatar": {"fileId": "9f3c…"}}
}
```

`FetchFileFromCacheDeserializer` resolves the id against the file holder during deserialisation, so
`avatar.getFile()` and `avatar.getBytes()` are populated by the time your service runs. The service
never talks to the store directly.

## Validators

Built on [`bard-validator`](https://github.com/bardframework/bard-validator), so file rules are
declared alongside the rest:

| Validator | Checks |
| --- | --- |
| `FileSizeValidator(long minSize, long maxSize)` | Byte size within bounds. |
| `FileContentTypeValidator(Set<String> validContentTypes)` | Content type against an allow-list. |
| `PictureAspectRatioValidator(double aspectRatio, double tolerance)` | Image aspect ratio within a tolerance — `1.0` for a square avatar, `1.777` for 16:9. |

Each also has an overload taking an `errorCode` so the message can be specific to the field.

## License

Apache License 2.0.
