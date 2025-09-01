package com.michal.grud.movieCategorizationSystem.domain.movie.repository;

import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MovieRepositoryTest {
    @Autowired
    private MovieRepository repo;

    private void seed() {
        repo.saveAll(List.of(
                MovieEntity.builder()
                        .title("A")
                        .productionYear("1999")
                        .director("Dir A")
                        .fileSize(100L)
                        .rankScore(1)
                        .fileId("fa")
                        .build(),
                MovieEntity.builder()
                        .title("B")
                        .productionYear("2001")
                        .director("Dir B")
                        .fileSize(300L)
                        .rankScore(3)
                        .fileId("fb")
                        .build(),
                MovieEntity.builder()
                        .title("C")
                        .productionYear("2000")
                        .director("Dir C")
                        .fileSize(200L)
                        .rankScore(2)
                        .fileId("fc")
                        .build()
        ));
    }

    @Test
    void shouldSaveAndFindById() {
        MovieEntity m = MovieEntity.builder()
                .title("The Matrix")
                .productionYear("1999")
                .director("The Wachowskis")
                .fileSize(1_234_567L)
                .rankScore(95)
                .fileId("file-123")
                .build();

        repo.save(m);

        var found = repo.findById("The Matrix");
        assertThat(found).isPresent();
        assertThat(found.get().getDirector()).isEqualTo("The Wachowskis");
    }

    @Test
    void shouldExistsAndDelete() {
        repo.save(MovieEntity.builder().title("X").build());
        assertThat(repo.existsById("X")).isTrue();

        repo.deleteById("X");
        assertThat(repo.existsById("X")).isFalse();
    }

    @Test
    void shouldSortByFileSize() {
        seed();

        Page<MovieEntity> asc = repo.findAll(PageRequest.of(0, 10, Sort.by("fileSize").ascending()));
        assertThat(asc.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("A", "C", "B");

        Page<MovieEntity> desc = repo.findAll(PageRequest.of(0, 10, Sort.by("fileSize").descending()));
        assertThat(desc.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("B", "C", "A");
    }

    @Test
    void shouldSortByRankScore() {
        seed();

        Page<MovieEntity> asc = repo.findAll(PageRequest.of(0, 10, Sort.by("rankScore").ascending()));
        assertThat(asc.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("A", "C", "B");

        Page<MovieEntity> desc = repo.findAll(PageRequest.of(0, 10, Sort.by("rankScore").descending()));
        assertThat(desc.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("B", "C", "A");
    }

    @Test
    void shouldPageAndSortTogether() {
        seed();

        Pageable p0 = PageRequest.of(0, 2, Sort.by("fileSize").descending());
        Pageable p1 = PageRequest.of(1, 2, Sort.by("fileSize").descending());

        Page<MovieEntity> firstPage = repo.findAll(p0);
        Page<MovieEntity> secondPage = repo.findAll(p1);

        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);

        assertThat(firstPage.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("B", "C");

        assertThat(secondPage.getContent()).extracting(MovieEntity::getTitle)
                .containsExactly("A");
    }
}
