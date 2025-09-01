package com.michal.grud.movieCategorizationSystem.domain.movie.mapper;

import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MovieMapperTest {
    @Test
    void mapToMovieEntity_shouldBuildEntityWithAllFields() {
        String title = "The Matrix";
        String year = "1999";
        String director = "The Wachowskis";
        String fileId = "file-123";
        long fileSize = 1_234_567L;
        int rankScore = 95;

        MovieEntity entity = MovieMapper.mapToMovieEntity(title, year, director, fileId, fileSize, rankScore);

        assertThat(entity.getTitle()).isEqualTo(title);
        assertThat(entity.getProductionYear()).isEqualTo(year);
        assertThat(entity.getDirector()).isEqualTo(director);
        assertThat(entity.getFileId()).isEqualTo(fileId);
        assertThat(entity.getFileSize()).isEqualTo(fileSize);
        assertThat(entity.getRankScore()).isEqualTo(rankScore);
    }

    @Test
    void mapToMovieDetailsDTO_shouldMapAllFieldsFromEntity() {
        MovieEntity entity = MovieEntity.builder()
                .title("The Lord of the Rings")
                .productionYear("2001")
                .director("Peter Jackson")
                .rankScore(99)
                .fileSize(5_000_000_000L)
                .fileId("lotr-001")
                .build();

        MovieDetailsDTO dto = MovieMapper.mapToMovieDetailsDTO(entity);

        assertThat(dto.getTitle()).isEqualTo("The Lord of the Rings");
        assertThat(dto.getProductionYear()).isEqualTo("2001");
        assertThat(dto.getDirector()).isEqualTo("Peter Jackson");
        assertThat(dto.getRankScore()).isEqualTo(99);
        assertThat(dto.getFileSize()).isEqualTo(5_000_000_000L);
        assertThat(dto.getFileId()).isEqualTo("lotr-001");
    }

    @Test
    void mapToUpdatedMovieEntityWithRankScore_shouldReturnNewEntityWithOnlyRankUpdated() {
        MovieEntity original = MovieEntity.builder()
                .title("Avengers")
                .productionYear("2012")
                .director("Joss Whedon")
                .rankScore(88)
                .fileSize(2_000_000_000L)
                .fileId("av-001")
                .build();

        MovieEntity updated = MovieMapper.mapToUpdatedMovieEntityWithRankScore(original, 91);

        assertThat(original.getRankScore()).isEqualTo(88);

        assertThat(updated.getTitle()).isEqualTo(original.getTitle());
        assertThat(updated.getProductionYear()).isEqualTo(original.getProductionYear());
        assertThat(updated.getDirector()).isEqualTo(original.getDirector());
        assertThat(updated.getFileSize()).isEqualTo(original.getFileSize());
        assertThat(updated.getFileId()).isEqualTo(original.getFileId());

        assertThat(updated.getRankScore()).isEqualTo(91);
    }
}