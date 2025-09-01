package com.michal.grud.movieCategorizationSystem.common.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static java.util.Map.entry;

public class VideoFileTypeChecker {

    public static final String FILE_IS_NOT_A_VIDEO_FILE_MSG = "File is not a video file";
    public static final String COULD_NOT_RECOGNIZE_FILE_TYPE_MSG = "Could not recognize file type";
    private static final Map<String, String> EXT_TO_MIMES = Map.ofEntries(
            entry("video/mp4", "mp4"),
            entry("video/x-m4v", "m4v"),
            entry("video/quicktime", "mov"),
            entry("video/x-msvideo", "avi"),
            entry("video/x-matroska", "mkv"),
            entry("video/webm", "webm"),
            entry("video/mpeg", "mpeg"),
            entry("video/3gpp", "3gp"),
            entry("ts", "video/mp2t"),
            entry("video/3gpp2", "3g2"),
            entry("video/x-flv", "flv"),
            entry("video/x-ms-wmv", "wmv"));
    private static final Tika tika = new Tika();

    public static String detectMimeType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(COULD_NOT_RECOGNIZE_FILE_TYPE_MSG, e);
        }
    }

    public static String getVideoExtension(MultipartFile file) {
        String mimeType = detectMimeType(file);

        if (!EXT_TO_MIMES.containsKey(mimeType)) {
            throw new IllegalArgumentException(FILE_IS_NOT_A_VIDEO_FILE_MSG);
        }
        return EXT_TO_MIMES.get(mimeType);
    }
}