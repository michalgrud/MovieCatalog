package com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieEntityTest {
    @Test
    void builderShouldSetAllFields() {
        MovieEntity m = MovieEntity.builder()
                .title("The Matrix")
                .productionYear("1999")
                .director("The Wachowskis")
                .rankScore(95)
                .fileSize(1_234_567L)
                .fileId("matrix.mp4")
                .build();

        assertThat(m.getTitle()).isEqualTo("The Matrix");
        assertThat(m.getProductionYear()).isEqualTo("1999");
        assertThat(m.getDirector()).isEqualTo("The Wachowskis");
        assertThat(m.getRankScore()).isEqualTo(95);
        assertThat(m.getFileSize()).isEqualTo(1_234_567L);
        assertThat(m.getFileId()).isEqualTo("matrix.mp4");
    }

    @Test
    void toBuilderShouldCopyAndAllowSingleFieldChange() {
        MovieEntity original = MovieEntity.builder()
                .title("LOTR")
                .productionYear("2001")
                .director("Peter Jackson")
                .rankScore(90)
                .fileSize(5_000_000_000L)
                .fileId("lotr-1.mp4")
                .build();

        MovieEntity updated = original.toBuilder()
                .rankScore(99)
                .build();

        assertThat(original.getRankScore()).isEqualTo(90);

        assertThat(updated.getTitle()).isEqualTo("LOTR");
        assertThat(updated.getProductionYear()).isEqualTo("2001");
        assertThat(updated.getDirector()).isEqualTo("Peter Jackson");
        assertThat(updated.getFileSize()).isEqualTo(5_000_000_000L);
        assertThat(updated.getFileId()).isEqualTo("lotr-1.mp4");
        assertThat(updated.getRankScore()).isEqualTo(99);
    }

    @Test
    void noArgsConstructorShouldCreateObjectWithNullFields() {
        MovieEntity empty = new MovieEntity();
        assertThat(empty.getTitle()).isNull();
        assertThat(empty.getProductionYear()).isNull();
        assertThat(empty.getDirector()).isNull();
        assertThat(empty.getRankScore()).isNull();
        assertThat(empty.getFileSize()).isNull();
        assertThat(empty.getFileId()).isNull();
    }

    @Test
    void shouldHaveJpaAnnotationsWithProperTableAndIndexes() throws Exception {
        Class<MovieEntity> clazz = MovieEntity.class;

        assertThat(clazz.isAnnotationPresent(Entity.class)).isTrue();

        assertThat(clazz.isAnnotationPresent(Table.class)).isTrue();
        Table table = clazz.getAnnotation(Table.class);
        assertThat(table.name()).isEqualTo("movies");

        Index[] idx = table.indexes();
        assertThat(idx).isNotNull();
        assertThat(Arrays.stream(idx).map(Index::name)).contains("idx_movies_size", "idx_movies_rank");
        assertThat(Arrays.stream(idx).map(Index::columnList)).contains("fileSize", "rankScore");

        Field titleField = clazz.getDeclaredField("title");
        assertThat(titleField.isAnnotationPresent(Id.class)).isTrue();
    }
}