package com.michal.grud.movieCategorizationSystem.domain.movie.repository;

import com.michal.grud.movieCategorizationSystem.domain.movie.entity.jpa.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, String> {
}
