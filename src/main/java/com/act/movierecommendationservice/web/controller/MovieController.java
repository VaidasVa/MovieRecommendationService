package com.act.movierecommendationservice.web.controller;

import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping("/all")
    public ResponseEntity<Optional<List<Movie>>> getAllMovies() {
        Optional<List<Movie>> movies = service.getAllMovies().join();
        if (movies.get().isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(service.getAllMovies().join(), HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    private ResponseEntity<Optional<List<Movie>>> getMoviesByTitle(@NonNull @NotBlank @PathVariable String title) {
        Optional<List<Movie>> movies = service.getMovieByTitle(title).join();
        if (movies.get().isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PostMapping("/addMovie")
    public ResponseEntity<String> addMovie(@Valid @RequestBody Movie movie) {
        boolean saved = service.postMovie(movie);
        if (saved) return new ResponseEntity<>("Movie saved.", HttpStatus.OK);
        else return new ResponseEntity<>("Movie already exists, could not be saved.", HttpStatus.CONFLICT);
    }

    @PostMapping("/{id}/{rating}")
    public ResponseEntity<String> rateMovie(@PathVariable String id, @PathVariable int rating) {
        if (rating > 5 || rating < 1) {
            return new ResponseEntity<>("Invalid rating", HttpStatus.BAD_REQUEST);
        } else {
            service.rateMovie(id, rating);
            String newRating = "New rating " + service.getRating(id);
            return new ResponseEntity<>(newRating, HttpStatus.OK);
        }
    }

    @PutMapping
    public ResponseEntity<String> updateMovie(@Valid @RequestBody Movie movie) {
        service.updateMovie(movie);
        return new ResponseEntity<>("Updated.", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable String id) {
        service.deleteMovie(id);
        return new ResponseEntity<>("Deleted.", HttpStatus.NO_CONTENT);
    }
}
