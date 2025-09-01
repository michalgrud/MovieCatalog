package com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movies_size", columnList = "fileSize"),
        @Index(name = "idx_movies_rank", columnList = "rankScore")
})
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {

    @Id
    private String title;
    private String productionYear;
    private String director;
    private Integer rankScore;
    private Long fileSize;
    private String fileId;
}
