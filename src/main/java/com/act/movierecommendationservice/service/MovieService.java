package com.act.movierecommendationservice.service;

import com.act.movierecommendationservice.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MovieService {
    CompletableFuture<Optional<List<Movie>>> getAllMovies();

    CompletableFuture<Optional<Movie>> getMovieById(String id);

    CompletableFuture<Optional<List<Movie>>> getMovieByTitle(String title);

    boolean postMovie(Movie movie);

    void updateMovie(Movie movie);

    void rateMovie(String id, int rate);

    double getRating(String id);

    void deleteMovie(String id);
}
