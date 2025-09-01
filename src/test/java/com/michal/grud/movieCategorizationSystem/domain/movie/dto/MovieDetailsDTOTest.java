package com.michal.grud.movieCategorizationSystem.domain.movie.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MovieDetailsDTOTest {
    @Test
    void builderShouldSetAllFields() {
        MovieDetailsDTO dto = MovieDetailsDTO.builder()
                .title("The Matrix")
                .director("Lana Wachowski, Lilly Wachowski")
                .productionYear("1999")
                .fileId("file-123")
                .fileSize(1_234_567L)
                .rankScore(95)
                .build();

        assertThat(dto.getTitle()).isEqualTo("The Matrix");
        assertThat(dto.getDirector()).contains("Wachowski");
        assertThat(dto.getProductionYear()).isEqualTo("1999");
        assertThat(dto.getFileId()).isEqualTo("file-123");
        assertThat(dto.getFileSize()).isEqualTo(1_234_567L);
        assertThat(dto.getRankScore()).isEqualTo(95);
    }

    @Test
    void settersShouldModifyValues() {
        MovieDetailsDTO dto = MovieDetailsDTO.builder()
                .title("Old")
                .director("Dir")
                .productionYear("2000")
                .fileId("id")
                .fileSize(10L)
                .rankScore(1)
                .build();

        dto.setTitle("New");
        dto.setRankScore(2);
        dto.setFileSize(20L);

        assertThat(dto.getTitle()).isEqualTo("New");
        assertThat(dto.getRankScore()).isEqualTo(2);
        assertThat(dto.getFileSize()).isEqualTo(20L);
    }

    @Test
    void equalsAndHashCodeShouldUseAllFields() {
        MovieDetailsDTO a = MovieDetailsDTO.builder()
                .title("Same")
                .director("Dir")
                .productionYear("2001")
                .fileId("X")
                .fileSize(100L)
                .rankScore(50)
                .build();

        MovieDetailsDTO b = MovieDetailsDTO.builder()
                .title("Same")
                .director("Dir")
                .productionYear("2001")
                .fileId("X")
                .fileSize(100L)
                .rankScore(50)
                .build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());

        b.setRankScore(51);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toStringShouldContainClassNameAndKeyFields() {
        MovieDetailsDTO dto = MovieDetailsDTO.builder()
                .title("Showcase")
                .director("Someone")
                .productionYear("2020")
                .fileId("f")
                .fileSize(1L)
                .rankScore(10)
                .build();

        String s = dto.toString();
        assertThat(s).contains("MovieDetailsDTO");
        assertThat(s).contains("title=Showcase");
        assertThat(s).contains("productionYear=2020");
    }
}