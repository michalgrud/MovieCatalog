package com.michal.grud.movieCategorizationSystem.domain.movie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieDetailsDTO {
    private String title;
    private String director;
    private String productionYear;
    private String fileId;
    private Long fileSize;
    private Integer rankScore;
}
