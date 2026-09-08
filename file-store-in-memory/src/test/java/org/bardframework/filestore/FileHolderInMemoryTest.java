package org.bardframework.filestore;

import org.bardframework.filestore.file.FileInfo;
import org.bardframework.filestore.file.FileInfoImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileHolderInMemoryTest {

    private static final String USER = "user-1";
    private static final String OTHER_USER = "user-2";

    private static FileInfo file(String content) {
        return new FileInfoImpl(content.getBytes(StandardCharsets.UTF_8), "photo.png", "image/png");
    }

    private static FileHolderInMemory<String> holder() {
        return new FileHolderInMemory<>(30, ChronoUnit.MINUTES) {
        };
    }

    @Test
    void save_thenGet_returnsTheFile() {
        FileHolderInMemory<String> holder = holder();

        String key = holder.save(file("hello"), USER);

        assertThat(key).isNotBlank();
        assertThat(holder.get(key, USER).getBytes()).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("the key is a generated identifier, not the client-supplied file name")
    void save_generatesKeyIndependentOfFileName() {
        FileHolderInMemory<String> holder = holder();

        String key = holder.save(file("hello"), USER);

        assertThat(key).doesNotContain("photo.png").doesNotContain("..").doesNotContain("/");
    }

    @Test
    @DisplayName("keys are unique so one upload cannot overwrite another")
    void save_generatesUniqueKeys() {
        FileHolderInMemory<String> holder = holder();
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            keys.add(holder.save(file("content-" + i), USER));
        }
        assertThat(keys).hasSize(200);
    }

    @Test
    void get_returnsNullForUnknownKey() {
        assertThat(holder().get("unknown", USER)).isNull();
    }

    @Test
    @DisplayName("a file held for one user is not readable by another")
    void get_isScopedToTheOwningUser() {
        FileHolderInMemory<String> holder = holder();
        String key = holder.save(file("secret"), USER);

        assertThat(holder.get(key, OTHER_USER)).isNull();
    }

    @Test
    void remove_deletesTheFile() {
        FileHolderInMemory<String> holder = holder();
        String key = holder.save(file("hello"), USER);

        assertThat(holder.remove(key, USER)).isTrue();
        assertThat(holder.get(key, USER)).isNull();
    }

    @Test
    void remove_reportsFalseForUnknownKey() {
        assertThat(holder().remove("unknown", USER)).isFalse();
    }

    @Test
    void save_rejectsNullFileAndNullUser() {
        FileHolderInMemory<String> holder = holder();

        assertThatThrownBy(() -> holder.save(null, USER)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> holder.save(file("hello"), null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_rejectsBlankKeyAndNullUser() {
        FileHolderInMemory<String> holder = holder();

        assertThatThrownBy(() -> holder.get("", USER)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> holder.get("some-key", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
