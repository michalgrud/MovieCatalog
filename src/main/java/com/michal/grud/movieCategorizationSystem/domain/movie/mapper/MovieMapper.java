package com.michal.grud.movieCategorizationSystem.domain.movie.mapper;

import com.michal.grud.movieCategorizationSystem.domain.movie.dto.MovieDetailsDTO;
import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;

public class MovieMapper {

    public static MovieEntity mapToMovieEntity(String title, String year, String director, String fileId, long fileSize, int rankScore) {
        return MovieEntity.builder()
                .director(director)
                .title(title)
                .fileSize(fileSize)
                .fileId(fileId)
                .productionYear(year)
                .rankScore(rankScore)
                .build();
    }

    public static MovieDetailsDTO mapToMovieDetailsDTO(MovieEntity entity) {
        return MovieDetailsDTO.builder()
                .director(entity.getDirector())
                .title(entity.getTitle())
                .productionYear(entity.getProductionYear())
                .fileSize(entity.getFileSize())
                .rankScore(entity.getRankScore())
                .fileId(entity.getFileId())
                .build();
    }


    public static MovieEntity mapToUpdatedMovieEntityWithRankScore(MovieEntity entity, int rankScore) {
        return entity.toBuilder()
                .rankScore(rankScore)
                .build();
    }
}
