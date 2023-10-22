package com.act.movierecommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieDAO, String> {
    List<MovieDAO> findAllByTitleIsLikeIgnoreCase(String title);

    List<MovieDAO> findAllByGenreIs(String genre);

    @Query(value = "SELECT m FROM MovieDAO m ORDER BY m.avgUserRating DESC LIMIT 5")
    List<MovieDAO> findAllByOrderByAvgUserRating();
}
