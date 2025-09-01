package com.michal.grud.movieCategorizationSystem.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static com.michal.grud.movieCategorizationSystem.common.util.VideoFileTypeChecker.FILE_IS_NOT_A_VIDEO_FILE_MSG;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class VideoFileTypeCheckerTest {
    private static final byte[] MINIMAL_MP4_HEADER = new byte[]{
            0x00, 0x00, 0x00, 0x18, // size 24
            0x66, 0x74, 0x79, 0x70, // 'f','t','y','p'
            0x6D, 0x70, 0x34, 0x32, // 'm','p','4','2'
            0x00, 0x00, 0x00, 0x00,
            0x6D, 0x70, 0x34, 0x32, // 'm','p','4','2'
            0x69, 0x73, 0x6F, 0x6D  // 'i','s','o','m'
    };

    @Test
    void shouldReturnMp4ExtensionForMp4Bytes() {
        var file = new MockMultipartFile("file", "video.mp4", "video/mp4", MINIMAL_MP4_HEADER);

        String ext = VideoFileTypeChecker.getVideoExtension(file);

        assertThat(ext).isEqualTo("mp4");
    }

    @Test
    void shouldThrowForNonVideoBytes() {
        var notVideo = new MockMultipartFile("file", "data.json", "application/json", "{ \"a\":1 }".getBytes());

        assertThatThrownBy(() -> VideoFileTypeChecker.getVideoExtension(notVideo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(FILE_IS_NOT_A_VIDEO_FILE_MSG);
    }
}