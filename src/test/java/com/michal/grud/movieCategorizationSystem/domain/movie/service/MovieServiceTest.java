package com.michal.grud.movieCategorizationSystem.domain.movie.service;


import com.michal.grud.movieCategorizationSystem.common.exception.GlobalValidationException;
import com.michal.grud.movieCategorizationSystem.common.util.VideoFileTypeChecker;
import com.michal.grud.movieCategorizationSystem.domain.movie.client.files.FilesStorage;
import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import com.michal.grud.movieCategorizationSystem.domain.movie.repository.MovieRepository;
import com.michal.grud.movieCategorizationSystem.domain.movie.service.vlidation.MovieValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    @Mock
    private MovieRankScoreCalculator rankCalculator;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private FilesStorage filesStorage;
    @Mock
    private MovieValidator movieValidator;

    @InjectMocks
    private MovieService service;

    @Test
    void createUpdateMovie_shouldValidate_GenerateFileId_SaveAndReturnDTO() {
        String title = "The Matrix";
        String year = "1999";
        String director = "The Wachowskis";
        byte[] bytes = "dummy-video".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "any.bin", "application/octet-stream", bytes);

        int computedRank = 150;

        try (MockedStatic<VideoFileTypeChecker> mocked = mockStatic(VideoFileTypeChecker.class)) {
            mocked.when(() -> VideoFileTypeChecker.getVideoExtension(file)).thenReturn("mp4");

            when(rankCalculator.calculateRankScore(title, (long) bytes.length)).thenReturn(computedRank);
            when(movieRepository.saveAndFlush(any(MovieEntity.class)))
                    .thenAnswer(inv -> inv.getArgument(0, MovieEntity.class));

            ResponseEntity<MovieDetailsDTO> resp = service.createUpdateMovie(title, year, director, file);

            verify(movieValidator).validateMovieCreateUpdate(file);
            verify(rankCalculator).calculateRankScore(title, (long) bytes.length);

            String expectedFileId = "The Matrix.mp4";
            verify(filesStorage).save(file, expectedFileId);
            verify(movieRepository).saveAndFlush(argThat(e ->
                    e.getTitle().equals(title) &&
                            e.getProductionYear().equals(year) &&
                            e.getDirector().equals(director) &&
                            e.getFileSize() == bytes.length &&
                            e.getRankScore().equals(computedRank) &&
                            e.getFileId().equals(expectedFileId)
            ));

            assertThat(resp.getStatusCode().value()).isEqualTo(200);
            assertThat(resp.getBody()).isNotNull();
            assertThat(resp.getBody().getTitle()).isEqualTo(title);
            assertThat(resp.getBody().getFileId()).isEqualTo(expectedFileId);
            assertThat(resp.getBody().getRankScore()).isEqualTo(computedRank);
        }
    }


    @Test
    void deleteMovie_shouldRemoveFromRepoAndStorage_whenExists() {
        String title = "Kiler";
        MovieEntity entity = MovieEntity.builder()
                .title(title)
                .fileId("Kiler.mp4")
                .build();

        when(movieRepository.findById(title)).thenReturn(Optional.of(entity));

        ResponseEntity<Void> resp = service.deleteMovie(title);

        verify(movieRepository).delete(entity);
        verify(movieRepository).flush();
        verify(filesStorage).delete("Kiler.mp4");

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void deleteMovie_shouldThrow_whenNotFound() {
        when(movieRepository.findById("Missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteMovie("Missing"))
                .isInstanceOf(GlobalValidationException.class);
        verify(movieRepository).findById("Missing");
        verifyNoMoreInteractions(movieRepository, filesStorage);
    }


    @Test
    void search_shouldApplyPageableAndMapToDTO() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        MovieEntity e = MovieEntity.builder()
                .title("A")
                .productionYear("2000")
                .director("Dir")
                .fileId("A.mp4")
                .fileSize(200L)
                .rankScore(2)
                .build();

        Page<MovieEntity> page = new PageImpl<>(List.of(e), PageRequest.of(1, 2), 3);
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<MovieDetailsDTO> out = service.search("rankScore", "asc", 1, 2);

        verify(movieRepository).findAll(captor.capture());
        Pageable used = captor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(1);
        assertThat(used.getPageSize()).isEqualTo(2);
        assertThat(used.getSort()).isEqualTo(Sort.by(Sort.Order.asc("rankScore")));

        assertThat(out.getContent()).hasSize(1);
        assertThat(out.getContent().get(0).getTitle()).isEqualTo("A");
        assertThat(out.getTotalElements()).isEqualTo(3);
    }

    @Test
    void search_shouldThrowOnInvalidSortBy() {
        assertThatThrownBy(() -> service.search("title", "desc", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sortBy must be fileSize or rankScore");
        verifyNoInteractions(movieRepository);
    }

    @Test
    void downloadMovie_shouldStreamBytesAndSetHeaders() throws Exception {
        String title = "LOTR";
        String fileId = "LOTR.mp4";
        byte[] content = "stream-me".getBytes(StandardCharsets.UTF_8);

        MovieEntity entity = MovieEntity.builder().title(title).fileId(fileId).build();
        when(movieRepository.findById(title)).thenReturn(Optional.of(entity));
        when(filesStorage.load(fileId)).thenReturn(new ByteArrayInputStream(content));

        ResponseEntity<StreamingResponseBody> resp = service.downloadMovie(title);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getHeaders().getFirst("Content-Disposition")).contains("attachment");
        assertThat(resp.getHeaders().getFirst("Content-Disposition")).contains(fileId);
        assertThat(resp.getHeaders().getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(resp.getHeaders().getContentType()).isEqualTo(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resp.getBody().writeTo(out);
        assertThat(out.toByteArray()).isEqualTo(content);

        verify(filesStorage).load(fileId);
    }
}
