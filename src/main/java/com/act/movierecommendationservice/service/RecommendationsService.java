package com.act.movierecommendationservice.service;

import com.act.movierecommendationservice.model.Movie;

import java.util.List;

public interface RecommendationsService {
    List<Movie> getRecommendationsBasedOnJustWatchedMovie(String movieId);

    List<Movie> getRecommendationsBasedOnUserHistory(List<String> history);
}
