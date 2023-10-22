package com.act.movierecommendationservice.web.controller;

import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.service.MovieService;
import com.act.movierecommendationservice.service.RecommendationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationsController {

    private final MovieService movieService;
    private final RecommendationsService service;

    @GetMapping("/{movieId}")
    public ResponseEntity<List<Movie>> getRecommendations(@PathVariable String movieId) {
        if (movieService.getMovieById(movieId).join().isPresent()) {
            List<Movie> movies = service.getRecommendationsBasedOnJustWatchedMovie(movieId);
            return new ResponseEntity<>(movies, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Movie>> getRecommendationsBasedOnHistory(@RequestBody(required = false) List<String> history) {
        List<Movie> movies;
        if(history == null || history.isEmpty()) {
            movies = service.getRecommendationsBasedOnUserHistory(List.of());
        }
        else {
            movies = service.getRecommendationsBasedOnUserHistory(history);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
