package org.bardframework.filestore.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FileInfoImplTest {

    private static final byte[] CONTENT = "hello".getBytes(StandardCharsets.UTF_8);

    @Test
    void carriesTheGivenMetadataAndContent() {
        FileInfoImpl file = new FileInfoImpl(CONTENT, "photo.png", "image/png");

        assertThat(file.getName()).isEqualTo("photo.png");
        assertThat(file.getOriginalName()).isEqualTo("photo.png");
        assertThat(file.getContentType()).isEqualTo("image/png");
        assertThat(file.getBytes()).isEqualTo(CONTENT);
    }

    @Test
    @DisplayName("size is derived from the content, not supplied by the client")
    void sizeMatchesContentLength() {
        assertThat(new FileInfoImpl(CONTENT, "photo.png", "image/png").getSize()).isEqualTo(CONTENT.length);
    }

    @Test
    void emptyContentIsReportedAsEmpty() {
        assertThat(new FileInfoImpl(new byte[0], "photo.png", "image/png").isEmpty()).isTrue();
    }

    @Test
    void nonEmptyContentIsNotReportedAsEmpty() {
        assertThat(new FileInfoImpl(CONTENT, "photo.png", "image/png").isEmpty()).isFalse();
    }

    @Test
    @DisplayName("creation time is stamped so age-based backends can evict")
    void creationTimeIsStamped() {
        assertThat(new FileInfoImpl(CONTENT, "photo.png", "image/png").getCreateTime()).isNotNull();
    }

    @Test
    void contentTypeOnlyConstructorLeavesNameUnset() {
        FileInfoImpl file = new FileInfoImpl(CONTENT, "image/png");

        assertThat(file.getName()).isNull();
        assertThat(file.getContentType()).isEqualTo("image/png");
        assertThat(file.getBytes()).isEqualTo(CONTENT);
    }
}
