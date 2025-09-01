package com.michal.grud.movieCategorizationSystem.domain.movie.controller;

import com.michal.grud.movieCategorizationSystem.domain.movie.client.files.FilesStorage;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import com.michal.grud.movieCategorizationSystem.domain.movie.repository.MovieRepository;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.MovieRankScoreCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.michal.grud.movieCategorizationSystem.domain.movie.controller.MovieResource.BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "storage.local.root=${java.io.tmpdir}/movies-it"
})
public class MovieResourceIntTest {
    private static final byte[] MIN_MP4_HEADER = new byte[]{
            0, 0, 0, 24, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32, 0, 0, 0, 0, 0x6D, 0x70, 0x34, 0x32, 0x69, 0x73, 0x6F, 0x6D
    };
    @Autowired
    MockMvc mvc;
    @Autowired
    MovieRepository repo;
    @Autowired
    FilesStorage filesStorage;
    @Autowired
    MovieRankScoreCalculator rankCalculator;

    private static MovieEntity entity(String title, String year, String director, String fileId, long size, int rank) {
        return MovieEntity.builder()
                .title(title)
                .productionYear(year)
                .director(director)
                .fileId(fileId)
                .fileSize(size)
                .rankScore(rank)
                .build();
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    @BeforeEach
    void cleanDb() {
        repo.deleteAll();
    }

    @Test
    void upload_shouldPersistEntity_andFile_andReturnDto() throws Exception {
        byte[] content = concat(MIN_MP4_HEADER, "payload".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file = new MockMultipartFile(
                "file", "any.bin", "application/octet-stream", content);

        mvc.perform(multipart(BASE_PATH)
                        .file(file)
                        .param("title", "The Matrix")
                        .param("director", "The Wachowskis")
                        .param("year", "1999"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("The Matrix"))
                .andExpect(jsonPath("$.fileId").value("The Matrix.mp4"))
                .andExpect(jsonPath("$.productionYear").value("1999"))
                .andExpect(jsonPath("$.director").value("The Wachowskis"))
                .andExpect(jsonPath("$.fileSize").value(content.length))
                .andExpect(jsonPath("$.rankScore").value(100));

        Optional<MovieEntity> saved = repo.findById("The Matrix");
        assertThat(saved).isPresent();
        assertThat(saved.get().getFileId()).isEqualTo("The Matrix.mp4");
        try (InputStream in = filesStorage.load("The Matrix.mp4")) {
            byte[] stored = in.readAllBytes();
            assertThat(stored).startsWith(MIN_MP4_HEADER);
        }
    }

    @Test
    void search_shouldPageAndSort() throws Exception {
        repo.saveAll(List.of(
                entity("A", "1999", "DirA", "A.mp4", 100L, 1),
                entity("B", "2000", "DirB", "B.mp4", 300L, 3),
                entity("C", "2001", "DirC", "C.mp4", 200L, 2)
        ));

        mvc.perform(get(BASE_PATH).param("sortBy", "fileSize").param("dir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("B"))
                .andExpect(jsonPath("$.content[1].title").value("C"))
                .andExpect(jsonPath("$.content[2].title").value("A"))
                .andExpect(jsonPath("$.totalElements").value(3));

        mvc.perform(get(BASE_PATH)
                        .param("sortBy", "rankScore").param("dir", "asc")
                        .param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("B"))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void download_shouldReturnAttachment_withBytes() throws Exception {
        repo.save(entity("LOTR", "2001", "PJ", "LOTR.mp4", 10L, 10));
        filesStorage.save(new MockMultipartFile(
                "file", "LOTR.mp4", "video/mp4", concat(MIN_MP4_HEADER, "video".getBytes())), "LOTR.mp4");

        mvc.perform(get(BASE_PATH + "/download").param("title", "LOTR"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(header().string("Content-Disposition", containsString("LOTR.mp4")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().bytes(concat(MIN_MP4_HEADER, "video".getBytes())));
    }


    @Test
    void delete_shouldRemoveFromDbAndDisk() throws Exception {
        repo.save(entity("Kiler", "1997", "Machulski", "Kiler.mp4", 7L, 77));
        filesStorage.save(new MockMultipartFile(
                "file", "Kiler.mp4", "video/mp4", "x".getBytes()), "Kiler.mp4");

        mvc.perform(delete(BASE_PATH + "/{title}", "Kiler"))
                .andExpect(status().isOk());

        assertThat(repo.findById("Kiler")).isNotPresent();
        assertThatThrownBy(() -> filesStorage.load("Kiler.mp4")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void recalculate_shouldUpdateRanksAndReturnDtos() throws Exception {
        repo.saveAll(List.of(
                entity("Kiler", "2000", "D", "A.mp4", 10L, 0),
                entity("The Avengers", "2000", "D", "B.mp4", 20L, 0)
        ));

        mvc.perform(post(BASE_PATH + "/recalculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title=='Kiler')].rankScore").value(org.hamcrest.Matchers.contains(100)))
                .andExpect(jsonPath("$[?(@.title=='Kiler')].productionYear").value(org.hamcrest.Matchers.contains("2000")))
                .andExpect(jsonPath("$[?(@.title=='Kiler')].director").value(org.hamcrest.Matchers.contains("D")))
                .andExpect(jsonPath("$[?(@.title=='Kiler')].fileId").value(org.hamcrest.Matchers.contains("A.mp4")))
                .andExpect(jsonPath("$[?(@.title=='Kiler')].fileSize").value(org.hamcrest.Matchers.contains(10)))
                .andExpect(jsonPath("$[?(@.title=='The Avengers')].rankScore").value(org.hamcrest.Matchers.contains(100)))
                .andExpect(jsonPath("$[?(@.title=='The Avengers')].productionYear").value(org.hamcrest.Matchers.contains("2000")))
                .andExpect(jsonPath("$[?(@.title=='The Avengers')].director").value(org.hamcrest.Matchers.contains("D")))
                .andExpect(jsonPath("$[?(@.title=='The Avengers')].fileId").value(org.hamcrest.Matchers.contains("B.mp4")))
                .andExpect(jsonPath("$[?(@.title=='The Avengers')].fileSize").value(org.hamcrest.Matchers.contains(20)));

        assertThat(repo.findById("Kiler")).get().extracting(MovieEntity::getRankScore).isEqualTo(100);
        assertThat(repo.findById("The Avengers")).get().extracting(MovieEntity::getRankScore).isEqualTo(100);
    }
}
