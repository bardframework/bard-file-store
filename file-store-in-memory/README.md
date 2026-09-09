file-store-in-memory
====================

[![Maven Central](https://img.shields.io/badge/maven--central-6.1.3-blue.svg)](https://repo1.maven.org/maven2/org/bardframework/filestore/file-store-in-memory/)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Heap storage with age-based eviction. The right choice for tests and single-node development — and the wrong one for a cluster.

Part of [**Bard File Store**](../README.md) · [Bard Framework](https://github.com/bardframework)

```xml
<dependency>
    <groupId>org.bardframework.filestore</groupId>
    <artifactId>file-store-in-memory</artifactId>
    <version>5.6.3</version>
</dependency>
```

```java
@Bean
public UserFileHolder<FileInfo, AppUser> fileHolder() {
    return new FileHolderInMemory<AppUser>(30, ChronoUnit.MINUTES) { };
}
```

`FileHolderInMemory<U>` is abstract — the anonymous subclass binds your user type. Entries older than
the configured age are evicted.

## When not to use it

* **More than one node.** An upload that lands on node A is invisible to node B, so the form submit
  fails roughly half the time behind a load balancer.
* **Large files or high volume.** Every held file is live heap until it is evicted; a burst of
  uploads is a burst of memory.

Use [`file-store-redis`](../file-store-redis) or [`file-store-fs`](../file-store-fs) in production.

## When to use it

Integration tests, local development, and single-instance deployments where the operational
simplicity of having no extra dependency is worth more than the constraints.

## License

Apache License 2.0.
